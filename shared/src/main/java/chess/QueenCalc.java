package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenCalc {
    QueenCalc() {
    }


    public static Collection<ChessMove> run(ChessBoard myBoard, ChessPosition myPosition) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return PieceMoveCalc.slidingPiece(myBoard, myPosition, directions);
    }
}
        /*Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = myBoard.getPiece(myPosition).getTeamColor();

        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();

        while (cRow < 8 ){
            cRow++;
            //cCol++;
            ChessPosition endP = new ChessPosition(cRow,cCol);

            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));
            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == color) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            //System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }

        cRow = myPosition.getRow();
        cCol = myPosition.getColumn();
        while (cRow > 1 ){
            cRow--;
            //cCol++;
            ChessPosition endP = new ChessPosition(cRow,cCol);

            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));
            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == color) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            //System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }


        cRow = myPosition.getRow();
        cCol = myPosition.getColumn();
        while (cCol < 8 ){
            cCol++;
            ChessPosition endP = new ChessPosition(cRow,cCol);

            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));
            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == color) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            //System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }

        cRow = myPosition.getRow();
        cCol = myPosition.getColumn();
        while (cCol > 1 ){
            cCol--;
            ChessPosition endP = new ChessPosition(cRow,cCol);

            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));
            if(myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == color) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }
            //System.out.println("added Chess move 5, 4 to " + cRow + ", " + cCol);



        }

        cRow = myPosition.getRow();
        cCol = myPosition.getColumn();
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
        while (cRow < 8 && cCol > 1) {
            cRow++;
            cCol--;
            ChessPosition endP = new ChessPosition(cRow, cCol);


            //System.out.println("Get piece at current target position says " + myBoard.getPiece(endP));
            if (myBoard.getPiece(endP) == null) {
                moves.add(new ChessMove(myPosition, endP, null));
            } else if (myBoard.getPiece(endP).getTeamColor() == myBoard.getPiece(myPosition).getTeamColor()) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, endP, null));
                break;
            }

        }

        return moves;
    }
}*/
