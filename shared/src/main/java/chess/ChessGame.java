package chess;

import java.util.Collection;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard myBoard = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessMove lastMove;

    public ChessGame() {
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = myBoard.getPiece(startPosition).pieceMoves(myBoard,startPosition);
        ChessPiece piece = myBoard.getPiece(startPosition);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
            int row = startPosition.getRow();
            int col = startPosition.getColumn();
            TeamColor color = piece.getTeamColor();

            if (col > 1){
                ChessPosition left = new ChessPosition(row, col - 1);
                ChessPiece target = myBoard.getPiece(left);

                if(target != null){
                    if (target.getPieceType() == ChessPiece.PieceType.PAWN && target.getTeamColor() != color){
                        if(color == TeamColor.WHITE){
                            if (lastMove.getStartPosition().getRow() == 7){
                                ChessPosition endP = new ChessPosition(row + 1, col - 1);
                                ChessMove newMove = new ChessMove(startPosition, endP, null);
                                moves.add(newMove);
                                System.out.println("Added en WHITE passant move " + endP.getRow() + ", " + endP.getColumn());
                            }
                        }else{
                            if (lastMove.getStartPosition().getRow() == 2){
                                ChessPosition endP = new ChessPosition(row - 1, col - 1);
                                ChessMove newMove = new ChessMove(startPosition, endP, null);
                                moves.add(newMove);
                                System.out.println("Added BLACK en passant move " + endP.getRow() + ", " + endP.getColumn());
                            }
                        }
                    }
                }
            }
            if (col < 8){
                ChessPosition right = new ChessPosition(row, col + 1);
                ChessPiece target = myBoard.getPiece(right);

                if(target != null){
                    if (target.getPieceType() == ChessPiece.PieceType.PAWN && target.getTeamColor() != color){
                        if(color == TeamColor.WHITE){
                            if (lastMove.getStartPosition().getRow() == 7){
                                ChessPosition endP = new ChessPosition(row + 1, col + 1);
                                ChessMove newMove = new ChessMove(startPosition, endP, null);
                                moves.add(newMove);
                                System.out.println("Added WHITE en passant move " + endP.getRow() + ", " + endP.getColumn());
                            }
                        }else{
                            if (lastMove.getStartPosition().getRow() == 2){
                                ChessPosition endP = new ChessPosition(row - 1, col + 1);
                                ChessMove newMove = new ChessMove(startPosition, endP, null);
                                moves.add(newMove);
                                System.out.println("Added BLACKkkk en passant move " + endP.getRow() + ", " + endP.getColumn());
                            }
                        }
                    }
                }
            }

        }


        moves.removeIf(move -> !testMove(move));


        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = myBoard.getPiece(move.getStartPosition());

        if (piece == null){
            throw new InvalidMoveException("No such piece at initial move position");
        }

        TeamColor pieceColor = piece.getTeamColor();
        if (pieceColor != getTeamTurn()){
            throw new InvalidMoveException("Invalid move wrong team turn");
        }

        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null){
            piece = new ChessPiece(pieceColor, move.getPromotionPiece());
        }

        Collection<ChessMove> moves = validMoves(startPos);

        if (moves.contains(move)){
            /// En passant tweak
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                if(startPos.getColumn() != endPos.getColumn() && myBoard.getPiece(endPos) == null){
                    ChessPosition target = new ChessPosition(startPos.getRow(), endPos.getColumn());
                    myBoard.addPiece(target, null);
                }
            }


            myBoard.addPiece(startPos, null);
            myBoard.addPiece(endPos, piece);





            lastMove = move;

            if(pieceColor == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            } else {setTeamTurn(TeamColor.WHITE);}

        } else {
            throw new InvalidMoveException("Illegal move ");
        }




    }
    /// Returns true if the move is valid and false if the move isn't valid
    public boolean testMove(ChessMove move){
        ChessPiece piece = myBoard.getPiece(move.getStartPosition());
        TeamColor color = piece.getTeamColor();
        ChessPiece targetPiece = myBoard.getPiece(move.getEndPosition());

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        myBoard.addPiece(startPos, null);
        myBoard.addPiece(endPos, piece);

        boolean goodMove = !isInCheck(color);


        myBoard.addPiece(startPos, piece);
        myBoard.addPiece(endPos, targetPiece);
        return goodMove;
    }



    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = myBoard.findKing(teamColor);
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition pos = new ChessPosition(i,j);
                if(myBoard.getPiece(pos) != null){
                    ChessPiece piece = myBoard.getPiece(pos);
                    Collection<ChessMove> moves = piece.pieceMoves(myBoard, pos);
                    for (ChessMove move : moves){
                        if (move.getEndPosition().equals(kingPos)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }

        boolean escapable = false;


        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = myBoard.getPiece(pos);

                if (piece != null){
                    if (piece.getTeamColor() == teamColor){
                        Collection<ChessMove> moves = validMoves(pos);

                        if (!moves.isEmpty()){
                            escapable = true;

                        }

                    }
                }
            }
        }
        return !escapable;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }

        boolean movable = false;

        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = myBoard.getPiece(pos);

                if (piece != null){
                    if (piece.getTeamColor() == teamColor){
                        Collection<ChessMove> moves = validMoves(pos);

                        if (!moves.isEmpty()){
                            movable = true;

                        }

                    }
                }
            }
        }
        return !movable;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        myBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }
}
