import chess.*;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        UserDAO userMemory = new MemoryUserDAO();
        var userService = new UserService(userMemory);

        Server myServer = new Server();

        myServer.run(8080);


    }
}