package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalc {


    public KingCalc(){}

    public static Collection<ChessMove> run(ChessBoard myBoard, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor color = myBoard.getPiece(myPosition).getTeamColor();

        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();


        int xRow = cRow;
        int xCol = cCol;

        if (cRow < 8){
            xRow++;
            ChessPosition endP = new ChessPosition(xRow, xCol);
            if (myBoard.getPiece(endP) == null){
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() != color){
                moves.add(new ChessMove(myPosition, endP, null));
            }

            if (cCol < 8){
                xCol++;
                endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                } else if (myBoard.getPiece(endP).getTeamColor() != color){
                    moves.add(new ChessMove(myPosition, endP, null));
                }
                xCol = cCol;
            }

            if (cCol > 1){
                xCol--;
                endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                } else if (myBoard.getPiece(endP).getTeamColor() != color){
                    moves.add(new ChessMove(myPosition, endP, null));
                }
                xCol = cCol;
            }

            xRow = cRow;


        }


        if (cRow > 1){
            xRow--;
            ChessPosition endP = new ChessPosition(xRow, xCol);
            if (myBoard.getPiece(endP) == null){
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() != color){
                moves.add(new ChessMove(myPosition, endP, null));
            }

            if (cCol < 8){
                xCol++;
                endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                } else if (myBoard.getPiece(endP).getTeamColor() != color){
                    moves.add(new ChessMove(myPosition, endP, null));
                }
                xCol = cCol;
            }

            if (cCol > 1){
                xCol--;
                endP = new ChessPosition(xRow, xCol);
                if (myBoard.getPiece(endP) == null){
                    moves.add(new ChessMove(myPosition, endP, null));
                } else if (myBoard.getPiece(endP).getTeamColor() != color){
                    moves.add(new ChessMove(myPosition, endP, null));
                }
                xCol = cCol;
            }
            xRow = cRow;



        }

        if (cCol > 1){
            xCol--;
            ChessPosition endP = new ChessPosition(xRow, xCol);
            if (myBoard.getPiece(endP) == null){
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() != color){
                moves.add(new ChessMove(myPosition, endP, null));
            }
            xCol = cCol;

        }

        if (cCol < 8){
            xCol++;
            ChessPosition endP = new ChessPosition(xRow, xCol);
            if (myBoard.getPiece(endP) == null){
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() != color){
                moves.add(new ChessMove(myPosition, endP, null));
            }
            //xCol = cCol;

        }





        return moves;
    }
}
