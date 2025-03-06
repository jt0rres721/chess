package chess;

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
}
