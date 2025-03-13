import chess.*;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);


        //var userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        Server myServer = new Server();

        myServer.run(8080);


    }
}