package chess;


import java.util.ArrayList;
import java.util.Collection;

public class KnightCalc {
    KnightCalc() {}

    public static Collection<ChessMove> run(ChessBoard myBoard, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = myBoard.getPiece(myPosition).getTeamColor();
        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();

        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : knightMoves) {
            int xRow = cRow + move[0];
            int xCol = cCol + move[1];
            if (xRow >= 1 && xRow <= 8 && xCol >= 1 && xCol <= 8) {
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null || myBoard.getPiece(endP).getTeamColor() != color) {
                    moves.add(new ChessMove(myPosition, endP, null));
                }
            }
        }
        return moves;
    }
}