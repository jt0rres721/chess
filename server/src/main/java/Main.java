import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args){
        System.out.println("♕ 240 Chess Server: Starting up...");

        Server myServer = new Server();
        myServer.run(8080);


    }
}