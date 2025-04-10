package server.websocket;


import com.google.gson.Gson;
import dataaccess.ServerException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

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
    public void onMessage(Session session, String message){
        try{
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            saveSession(username, session);

            switch (command.getCommandType()){
                case CONNECT -> connect(session, username, command);
                case LEAVE -> leaveGame(username);
                case RESIGN -> resign();
                case MAKE_MOVE -> makeMove();
            }
        } catch (UnauthorizedException ex){
            //sendMessage(session.getRemote(), );
        } catch (Exception ex){

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
        var message = String.format("%s joined the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var game = new Gson().toJson(gameService.getGame(gameID));
        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        manager.broadcast(username, notification);
        manager.send(username, loadGame);
    }

    private void makeMove() {

    }


    private void leaveGame(String username) throws IOException {//, Session session, UserGameCommand command){
        manager.remove(username);
        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        manager.broadcast(username, notification);
    }


    private void resign(){}


    private void sendMessage(RemoteEndpoint remote, String message){}
}
