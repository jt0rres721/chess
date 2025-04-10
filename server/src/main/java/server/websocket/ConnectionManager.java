package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String name, Session session){
        Connection connection = new Connection(name, session);
        connections.put(name, connection);
    }

    public void remove(String name){
        connections.remove(name);
    }

    public void broadcast(String exclude, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()){
            if (c.session.isOpen()){
                if(!c.visitorName.equals(exclude)){
                    c.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void sendUser(String username, ServerMessage notification) throws IOException {
        if (connections.get(username).session.isOpen()){
            connections.get(username).send(new Gson().toJson(notification));
        }
    }

    public void send(Session session, ServerMessage notification)throws IOException{
        if (session.isOpen()){
            session.getRemote().sendString(new Gson().toJson(notification));
        }
    }
}
