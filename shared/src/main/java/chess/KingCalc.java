package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalc {


    public static Collection<ChessMove> run(ChessBoard myBoard, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = myBoard.getPiece(myPosition).getTeamColor();
        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();

        int[] rowOffsets = {-1, 0, 1};
        int[] colOffsets = {-1, 0, 1};

        for (int rowOffset : rowOffsets) {
            for (int colOffset : colOffsets) {
                if (rowOffset == 0 && colOffset == 0) continue;

                int newRow = cRow + rowOffset;
                int newCol = cCol + colOffset;

                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition endP = new ChessPosition(newRow, newCol);
                    ChessPiece targetPiece = myBoard.getPiece(endP);
                    if (targetPiece == null || targetPiece.getTeamColor() != color) {
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
        }

        return moves;
    }

}

