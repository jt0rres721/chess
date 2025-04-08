package server.websocket;


import com.google.gson.Gson;
import dataaccess.ServerException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager manager = new ConnectionManager();
    private final UserService userService;


    public WebSocketHandler(UserService userService) {
        this.userService = userService;
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

    private void connect(Session session, String username, UserGameCommand command) throws IOException {
        System.out.println("WEBSOCKET HANDLER CONNECT WAS RAN");
        manager.add(username, session);
        var message = String.format("SERVER BROADCAST: %s joined the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        manager.broadcast(username, notification);
    }

    private void makeMove() {}


    private void leaveGame(String username){//, Session session, UserGameCommand command){
        manager.remove(username);
    }


    private void resign(){}


    private void sendMessage(RemoteEndpoint remote, String message){}
}
