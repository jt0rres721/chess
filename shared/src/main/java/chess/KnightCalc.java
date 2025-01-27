package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalc {
    KnightCalc() {}

    public static Collection<ChessMove> run (ChessBoard myBoard, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = myBoard.getPiece(myPosition).getTeamColor();

        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();


        /// Moving up and diagonally
        if (cRow < 7){
            int xRow = cRow + 2;
            if (cCol < 8){
                int xCol = cCol + 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
            if (cCol > 1){
                int xCol = cCol - 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
        }

        /// Moving down and diagonally
        if (cRow > 2){
            int xRow = cRow - 2;
            if (cCol < 8){
                int xCol = cCol + 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
            if (cCol > 1){
                int xCol = cCol - 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
        }


        /// moving right and diagonally
        if (cCol < 7){
            int xCol = cCol + 2;
            if (cRow < 8){
                int xRow = cRow + 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
            if (cRow > 1){
                int xRow = cRow - 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
        }

        /// moving left and diagonally
        if (cCol > 2){
            int xCol = cCol - 2;
            if (cRow < 8){
                int xRow = cRow + 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
            if (cRow > 1){
                int xRow = cRow - 1;
                ChessPosition endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                }else {
                    if (myBoard.getPiece(endP).getTeamColor() != color){
                        moves.add(new ChessMove(myPosition, endP, null));
                    }
                }
            }
        }

        ///ChessPosition endP = new ChessPosition(cRow,cCol);


        return moves;
    }
}
