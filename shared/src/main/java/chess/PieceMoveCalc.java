package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalc {

    public PieceMoveCalc() {

    }

    public static Collection<ChessMove> calc(ChessBoard myBoard, ChessPosition myPosition){
        ChessPiece.PieceType type = myBoard.getPiece(myPosition).getPieceType();
        if (type == ChessPiece.PieceType.KING){
            return KingCalc.run(myBoard, myPosition);
        } else if (type == ChessPiece.PieceType.BISHOP) {
            return BishopCalc.run(myBoard, myPosition);
        }else if (type == ChessPiece.PieceType.PAWN){
            return PawnCalc.run(myBoard, myPosition);
        }else if (type == ChessPiece.PieceType.ROOK){
            return RookCalc.run(myBoard, myPosition);
        } else if (type == ChessPiece.PieceType.QUEEN){
            return QueenCalc.run(myBoard, myPosition);
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            return KnightCalc.run(myBoard, myPosition);
        }
        throw new RuntimeException("Not implemented piece type");
    }

    public static Collection<ChessMove> slidingPiece (ChessBoard board, ChessPosition position, int[][] directions){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        for (int[] dir : directions) {
            int cRow = position.getRow(), cCol = position.getColumn();
            while (true) {
                cRow += dir[0];
                cCol += dir[1];
                if (!isValidPosition(cRow, cCol)) {
                    break;
                }
                ChessPosition endPos = new ChessPosition(cRow, cCol);
                ChessPiece piece = board.getPiece(endPos);
                if (piece == null) {
                    moves.add(new ChessMove(position, endPos, null));
                } else {
                    if (piece.getTeamColor() != color) {
                        moves.add(new ChessMove(position, endPos, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }

    public static boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
