package chess;

import java.util.Collection;


public class ChessGame {
    private ChessBoard myBoard = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;
    private GameStatus state;

    public ChessGame() {
        state = GameStatus.ONGOING;
        myBoard.resetBoard();
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public enum TeamColor {
        WHITE, BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = myBoard.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(myBoard, startPosition);
        moves.removeIf(move -> !testMove(move));
        return moves;
    }



    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = myBoard.getPiece(move.getStartPosition());
        validateMove(move, piece);
        executeMove(move, piece);

        if(isInCheckmate(TeamColor.BLACK) || isInCheckmate(TeamColor.WHITE)){
            state = GameStatus.OVER;
        }
        if(isInStalemate(TeamColor.WHITE) || isInStalemate(TeamColor.BLACK)){
            state = GameStatus.OVER;
        }
    }

    private void validateMove(ChessMove move, ChessPiece piece) throws InvalidMoveException {
        if (piece == null){
            throw new InvalidMoveException("No such piece at initial move position");
        }
        if (piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("Invalid move wrong team turn");
        }
        if (!validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException("Illegal move");
        }
    }

    private void executeMove(ChessMove move, ChessPiece piece) {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && startPos.getColumn() != endPos.getColumn() && myBoard.getPiece(endPos) == null) {
            ChessPosition target = new ChessPosition(startPos.getRow(), endPos.getColumn());
            myBoard.addPiece(target, null);
        }

        myBoard.addPiece(startPos, null);
        myBoard.addPiece(endPos, piece);
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean testMove(ChessMove move) {
        ChessPiece piece = myBoard.getPiece(move.getStartPosition());
        TeamColor color = piece.getTeamColor();
        ChessPiece targetPiece = myBoard.getPiece(move.getEndPosition());

        myBoard.addPiece(move.getStartPosition(), null);
        myBoard.addPiece(move.getEndPosition(), piece);
        boolean goodMove = !isInCheck(color);
        myBoard.addPiece(move.getStartPosition(), piece);
        myBoard.addPiece(move.getEndPosition(), targetPiece);

        return goodMove;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = myBoard.findKing(teamColor);
        return isUnderAttack(kingPos, teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = myBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && !validMoves(pos).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isUnderAttack(ChessPosition position, TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = myBoard.getPiece(pos);
                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }
                for (ChessMove move : piece.pieceMoves(myBoard, pos)) {
                    if (move.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setBoard(ChessBoard board) {
        myBoard = board;
    }

    public ChessBoard getBoard() {
        return myBoard;
    }

    public void endGame(){
        state = GameStatus.OVER;
    }

    public GameStatus getState(){
        return state;
    }
}
