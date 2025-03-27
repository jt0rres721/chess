import chess.*;
import server.ServerFacade;
import ui.Client;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var port = 8080;
        Repl repl = new Repl("http://localhost:" + port);
        Client client = new Client("http://localhost:" + port);
        ServerFacade facade = new ServerFacade("http://localhost:" + port);
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);

        repl.run();
    }
}