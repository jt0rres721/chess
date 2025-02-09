package chess;

import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard myBoard = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;
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
        TeamColor color = myBoard.getPiece(startPosition).getTeamColor();
        //ChessPosition kingPos = findPosition(color, ChessPiece.PieceType.KING);
        Collection<ChessMove> moves = myBoard.getPiece(startPosition).pieceMoves(myBoard,startPosition);
        Iterator<ChessMove> iterator = moves.iterator();

        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            if (!testMove(move)){
                iterator.remove();
            }

        }


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

        Collection<ChessMove> moves = myBoard.getPiece(startPos).pieceMoves(myBoard, startPos);

        if (moves.contains(move) && testMove(move)){
            myBoard.addPiece(startPos, null);
            myBoard.addPiece(endPos, piece);

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

        boolean goodMove = true;
        if (isInCheck(color)){
            goodMove = false;
        }


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

    public ChessPosition findPosition(TeamColor color, ChessPiece.PieceType type){
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition pos = new ChessPosition(i,j);
                if (myBoard.getPiece(pos) != null){
                    if (myBoard.getPiece(pos).getTeamColor() == color && myBoard.getPiece(pos).getPieceType() == type){
                        return pos;
                    }
                }
            }
        }
        throw new RuntimeException("Piece not found");
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor);  ///TODO Actually implement
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
