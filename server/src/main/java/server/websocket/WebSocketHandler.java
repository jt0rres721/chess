package server.websocket;


import chess.*;
import com.google.gson.Gson;
import dataaccess.ServerException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager manager = new ConnectionManager();
    private final UserService userService;
    private final GameService gameService;
    private boolean resign;


    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
        resign = false;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try{
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            saveSession(username, session, command.getGameID());

            switch (command.getCommandType()){
                case CONNECT -> connect(session, username, command);
                case LEAVE -> leaveGame(username, command);
                case RESIGN -> resign(session, username, command);
                case MAKE_MOVE -> makeMove(username, command);
                case CANCEL -> cancelResign();
            }
        } catch (UnauthorizedException ex){
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null);
            error.setErrorMessage( "Error: unauthorized");
            manager.send(session, error);
        } catch (Exception ex){
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null);
            error.setErrorMessage(ex.getMessage());
            manager.send(session, error);
        }
    }
    private void cancelResign(){
        resign = false;
    }

    private String getUsername(String authToken){
        try{
            return userService.verifyUser(authToken);
        } catch (ServerException ex){
            throw new UnauthorizedException("Error: unauthorized");
        }

    }

    private void saveSession(String name, Session session, int gameID){
        Connection connection = new Connection(name,session, gameID);
        if(!manager.connections.contains(connection)){
            manager.connections.put(name, connection);
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, ServerException {
        int gameID = command.getGameID();
        manager.add(username, session, command.getGameID());

        var file = gameService.getGame(gameID);
        String playerType;
        if(username.equals(file.whiteUsername())){
            playerType = "white";
        }else if(username.equals(file.blackUsername())){
            playerType = "black";
        } else {
            playerType = "an observer";
        }

        var message = String.format("%s joined the game as %s", username, playerType);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var game = new Gson().toJson(gameService.getChess(gameID));
        ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null);
        loadGame.setGame(game);
        manager.broadcast(username, notification, gameID);
        manager.sendUser(username, loadGame);
    }

    private void makeMove(String username, UserGameCommand command) throws IOException, ServerException {
        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        var info = gameService.getGame(gameID);
        ChessBoard brr = gameService.getChess(gameID).getBoard();
        ChessGame g = gameService.getChess(gameID);

        if(!Objects.equals(username, info.blackUsername()) && !Objects.equals(username, info.whiteUsername())){
            throw new ServerException("Error: Not a playing user", 400);
        }

        if(g.getState() == GameStatus.OVER){
            throw new ServerException("Error: Game has already ended", 400);
        }

        if(Objects.equals(username, info.whiteUsername()) && brr.getPiece(move.getStartPosition()).getTeamColor() ==
                ChessGame.TeamColor.BLACK){
            throw new ServerException("Error: Not your turn", 400);
        }
        if(Objects.equals(username, info.blackUsername()) && brr.getPiece(move.getStartPosition()).getTeamColor() ==
                ChessGame.TeamColor.WHITE){
            throw new ServerException("Error: Not your turn", 400);
        }

        gameService.makeMove(move, gameID);

        var game = gameService.getChess(gameID);

        var load = new Gson().toJson(game);
        ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null);
        loadGame.setGame(load);
        manager.broadcast(null, loadGame, gameID);

        String start = toChessPosition(move.getStartPosition());
        String end = toChessPosition(move.getEndPosition());
        var message = String.format("%s made a move from %s to %s", username, start, end);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        manager.broadcast(username, notification, gameID);

        if(game.getState() == GameStatus.OVER){
            var data = gameService.getGame(gameID);
            if(game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)){
               var checkMsg = "The game has resulted in a stalemate. Good game!";
               var serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMsg);
               manager.broadcast(null, serverMsg, gameID);
            } else if(game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)){
               var teamColor = (game.isInCheckmate(ChessGame.TeamColor.WHITE)) ? "White" : "Black";
               var user = (game.isInCheckmate(ChessGame.TeamColor.WHITE)) ? data.whiteUsername() : data.blackUsername();
               var checkMsg = String.format("%s user '%s' has been checkmated. Game over!", teamColor, user);
               var serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMsg);
               manager.broadcast(null, serverMsg, gameID);

            }

        } else if(game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)){
            var data = gameService.getGame(gameID);
            var teamColor = (game.isInCheck(ChessGame.TeamColor.WHITE)) ? "White" : "Black";
            var user = (game.isInCheck(ChessGame.TeamColor.WHITE)) ? data.whiteUsername() : data.blackUsername();
            var checkMsg = String.format("%s user '%s' is in check", teamColor, user);
            var serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMsg);
            manager.broadcast(null, serverMsg, gameID);
        }

    }

    private String toChessPosition(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        char file = (char) ('a' + (col - 1));
        return "" + file + row;
    }


    private void leaveGame(String username, UserGameCommand command) throws IOException, ServerException {
        manager.remove(username);
        gameService.leaveGame(username, command.getGameID());
        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        manager.broadcast(username, notification, command.getGameID());
    }


    private void resign(Session session, String username, UserGameCommand command) throws ServerException, IOException {
        int gameID = command.getGameID();
        var data = gameService.getGame(gameID);
        ChessGame g = gameService.getChess(gameID);

        if(g.getState() == GameStatus.OVER){
            throw new ServerException("Error: Game has ended already", 400);
        }

        if(!Objects.equals(username, data.whiteUsername()) && !Objects.equals(username, data.blackUsername())){
            throw new ServerException("Error: Not a playing user", 400);
        }

        if(resign){
            gameService.endGame(gameID);

            var load = new Gson().toJson(gameService.getChess(gameID));
            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, load);
            manager.broadcast(username, loadGame, gameID);

            manager.send(session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "You have resigned from the game"));

            var message = username + " resigned from the game";
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            manager.broadcast(username, notification, gameID);
            resign = false;

        } else {
            var message = "This action will result in forfeit of the game. Are you sure? [yes|no]";
            var msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            manager.send(session, msg);
            resign = true;
        }
    }

}
