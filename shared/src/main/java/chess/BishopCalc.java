package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalc {

    public static Collection<ChessMove> run(ChessBoard myBoard, ChessPosition myPosition) {
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return PieceMoveCalc.slidingPiece(myBoard, myPosition, directions);
    }
}
