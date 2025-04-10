package ui.websocket;

import com.google.gson.Gson;
import server.ServerException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ServerException {
        try{
            url = url.replace("http", "ws");
            URI socketUri = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>(){
                @Override
                public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                notificationHandler.notify(serverMessage);
            }});

        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ServerException(e.getMessage(), 500);
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws ServerException {
        try{
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex){
            throw new ServerException(ex.getMessage(), 500);
        }
    }

    public void leave(String authToken, int gameID) throws ServerException {
        try{
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch (IOException e) {
            throw new ServerException(e.getMessage(),500);
        }
    }

    public void makeMove(String authToken, int gameID) {

    }

    public void resign(String authToken, int gameID) throws ServerException {
        try{
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ServerException(e.getMessage(), 500);
        }
    }




}
