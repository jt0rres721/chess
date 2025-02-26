package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalc {

    public BishopCalc() {}

    public static Collection<ChessMove> run(ChessBoard myBoard, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        //System.out.println("At iteration 1");
        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();
        while (cRow < 8 && cCol < 8){
            cRow++;
            cCol++;
            ChessPosition endP = new ChessPosition(cRow,cCol);

            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));
            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == myBoard.getPiece(myPosition).getTeamColor()) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            //System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }

        cRow = myPosition.getRow();
        cCol = myPosition.getColumn();
        while (cRow > 1 && cCol < 8){
            cRow--;
            cCol++;
            ChessPosition endP = new ChessPosition(cRow,cCol);

            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));

            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == myBoard.getPiece(myPosition).getTeamColor()) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            //System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }


        cRow = myPosition.getRow();
        cCol = myPosition.getColumn();
        while (cRow > 1 && cCol > 1){
            cRow--;
            cCol--;
            ChessPosition endP = new ChessPosition(cRow,cCol);

            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));

            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == myBoard.getPiece(myPosition).getTeamColor()) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            //System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }


        cRow = myPosition.getRow();
        cCol = myPosition.getColumn();
        while (cRow < 8 && cCol > 1){
            cRow++;
            cCol--;
            ChessPosition endP = new ChessPosition(cRow,cCol);


            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));
            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == myBoard.getPiece(myPosition).getTeamColor()) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            ////System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }






        return moves;
    }
}
