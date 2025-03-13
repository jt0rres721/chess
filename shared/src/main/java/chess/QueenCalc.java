package chess;

import java.util.Collection;

public class QueenCalc {
    QueenCalc() {
    }


    public static Collection<ChessMove> run(ChessBoard myBoard, ChessPosition myPosition) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return PieceMoveCalc.slidingPiece(myBoard, myPosition, directions);
    }
}
