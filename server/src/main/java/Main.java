import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args){
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);


        //var userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        Server myServer = new Server();

        myServer.run(8080);


    }
}