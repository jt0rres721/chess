package chess;

import java.util.ArrayList;
import java.util.Collection;

class RookCalc {
    RookCalc() {}

    public static Collection<ChessMove> run(ChessBoard board, ChessPosition position) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return PieceMoveCalc.slidingPiece(board, position, directions);
    }
}
