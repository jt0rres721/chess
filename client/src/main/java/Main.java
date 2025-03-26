import chess.*;
import server.Server;
import server.ServerFacade;
import ui.Client;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        var port = server.run(0);
        Repl repl = new Repl("http://localhost:" + port);
        Client client = new Client("http://localhost:" + port, repl);
        ServerFacade facade = new ServerFacade("http://localhost:" + port);
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);

        repl.run();
    }
}