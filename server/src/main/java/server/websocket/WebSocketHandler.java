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


    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try{
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            saveSession(username, session);

            switch (command.getCommandType()){
                case CONNECT -> connect(session, username, command);
                case LEAVE -> leaveGame(username, command);
                case RESIGN -> resign(username, command);
                case MAKE_MOVE -> makeMove(username, command);
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

    private String getUsername(String authToken){
        try{
            return userService.verifyUser(authToken);
        } catch (ServerException ex){
            throw new UnauthorizedException("Error: unauthorized");
        }

    }

    private void saveSession(String name, Session session){
        Connection connection = new Connection(name,session);
        if(!manager.connections.contains(connection)){
            manager.connections.put(name, connection);
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, ServerException {
        int gameID = command.getGameID();
        manager.add(username, session);

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
        manager.broadcast(username, notification);
        manager.sendUser(username, loadGame);
    }

    private void makeMove(String username, UserGameCommand command) throws IOException, ServerException {
        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        var info = gameService.getGame(gameID);
        ChessBoard brr = gameService.getChess(gameID).getBoard();
        ChessGame g = gameService.getChess(gameID);

        if(g.getState() == GameStatus.OVER){
            throw new ServerException("Error: Game has already ended", 400);
        }

        if(!Objects.equals(username, info.blackUsername()) && !Objects.equals(username, info.whiteUsername())){
            throw new ServerException("Error: Not a playing user", 400);
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
        manager.broadcast(null, loadGame);

        String start = toChessPosition(move.getStartPosition());
        String end = toChessPosition(move.getEndPosition());
        var message = String.format("%s made a move from %s to %s", username, start, end);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        manager.broadcast(username, notification);

        if(game.getState() == GameStatus.OVER){
            var data = gameService.getGame(gameID);
            if(game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)){
               var checkMsg = "The game has resulted in a stalemate. Good game!";
               var serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMsg);
               manager.broadcast(null, serverMsg);
            } else if(game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)){
               var teamColor = (game.isInCheckmate(ChessGame.TeamColor.WHITE)) ? "White" : "Black";
               var user = (game.isInCheckmate(ChessGame.TeamColor.WHITE)) ? data.whiteUsername() : data.blackUsername();
               var checkMsg = String.format("%s user '%s' has been checkmated. Game over!", teamColor, user);
               var serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMsg);
               manager.broadcast(null, serverMsg);

            }

        } else if(game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)){
            var data = gameService.getGame(gameID);
            var teamColor = (game.isInCheck(ChessGame.TeamColor.WHITE)) ? "White" : "Black";
            var user = (game.isInCheck(ChessGame.TeamColor.WHITE)) ? data.whiteUsername() : data.blackUsername();
            var checkMsg = String.format("%s user '%s' is in check", teamColor, user);
            var serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMsg);
            manager.broadcast(null, serverMsg);
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
        manager.broadcast(username, notification);
    }


    private void resign(String username, UserGameCommand command) throws ServerException, IOException {
        int gameID = command.getGameID();
        gameService.endGame(gameID);

        var load = new Gson().toJson(gameService.getChess(gameID));
        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, load);
        manager.broadcast(username, loadGame);

        var message = username + " resigned from the game";
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        manager.broadcast(username, notification);
    }

}
