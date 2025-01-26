package chess;

import java.util.Collection;

public class PieceMoveCalc {

    public PieceMoveCalc() {

    }

    public static Collection<ChessMove> Calc(ChessBoard myBoard, ChessPosition myPosition){
        ChessPiece.PieceType type = myBoard.getPiece(myPosition).getPieceType();
        if (type == ChessPiece.PieceType.KING){
            return KingCalc.run(myBoard, myPosition);
        } else if (type == ChessPiece.PieceType.BISHOP) {
            return BishopCalc.run(myBoard, myPosition);
        }else if (type == ChessPiece.PieceType.PAWN){
            return PawnCalc.run(myBoard, myPosition);
        }
        throw new RuntimeException("Not implemented piece type");
    }
}
