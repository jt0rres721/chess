package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalc {

    public static Collection<ChessMove> run(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (direction == 1) ? 2 : 7;
        int promotionRow = (direction == 1) ? 8 : 1;


        ChessPosition oneStep = new ChessPosition(position.getRow() + direction, position.getColumn());
        if (board.isInBounds(oneStep) && board.getPiece(oneStep) == null) {
            addPawnMove(moves, position, oneStep, promotionRow);

            ChessPosition twoSteps = new ChessPosition(position.getRow() + 2 * direction, position.getColumn());
            if (position.getRow() == startRow && board.getPiece(twoSteps) == null) {
                moves.add(new ChessMove(position, twoSteps, null));
            }
        }


        int[] captureOffsets = {-1, 1};
        for (int offset : captureOffsets) {
            ChessPosition capturePos = new ChessPosition(position.getRow() + direction, position.getColumn() + offset);
            if (board.isInBounds(capturePos) && board.getPiece(capturePos) != null &&
                    board.getPiece(capturePos).getTeamColor() != board.getPiece(position).getTeamColor()) {
                addPawnMove(moves, position, capturePos, promotionRow);
            }
        }

        return moves;
    }

    private static void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int promotionRow) {
        if (end.getRow() == promotionRow) {
            for (ChessPiece.PieceType promotionType : new ChessPiece.PieceType[]{
                    ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT}) {
                moves.add(new ChessMove(start, end, promotionType));
            }
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}


