package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalc {
    public PawnCalc() {}

    public static Collection<ChessMove> run (ChessBoard myBoard, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = myBoard.getPiece(myPosition).getTeamColor();

        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();

        if (color == ChessGame.TeamColor.WHITE){
            if (cRow != 8){
                ChessPosition next = new ChessPosition(cRow + 1, cCol);
                if (myBoard.getPiece(next) == null){
                    if(cRow == 7){
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.BISHOP));
                    } else{
                        moves.add(new ChessMove(myPosition, next, null));
                    }
                    if (cRow == 2){
                        next = new ChessPosition(cRow + 2, cCol);
                        if (myBoard.getPiece(next) == null){
                            moves.add(new ChessMove(myPosition, next, null));
                        }
                    }
                }


                if (cCol != 1){
                    next = new ChessPosition(cRow + 1, cCol -1 );
                    if (myBoard.getPiece(next) != null){
                        if (myBoard.getPiece(next).getTeamColor() != color){
                            if(cRow == 7){
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.KNIGHT));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.BISHOP));
                            } else{
                                moves.add(new ChessMove(myPosition, next, null));
                            }
                        }

                    }
                }

                if (cCol != 8){
                    next = new ChessPosition(cRow + 1, cCol + 1);
                    if (myBoard.getPiece(next) != null){
                        if (myBoard.getPiece(next).getTeamColor() != color){
                            if(cRow == 7){
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.KNIGHT));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.BISHOP));
                            } else{
                                moves.add(new ChessMove(myPosition, next, null));
                            }
                        }

                    }
                }


            }







        }


        if (color == ChessGame.TeamColor.BLACK){
            if (cRow != 1){
                ChessPosition next = new ChessPosition(cRow - 1, cCol);
                if (myBoard.getPiece(next) == null){
                    if(cRow == 2){
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.BISHOP));
                    } else{
                        moves.add(new ChessMove(myPosition, next, null));
                    }
                    if (cRow == 7){
                        next = new ChessPosition(cRow - 2, cCol);
                        if (myBoard.getPiece(next) == null){
                            moves.add(new ChessMove(myPosition, next, null));
                        }
                    }
                }


                if (cCol != 1){
                    next = new ChessPosition(cRow - 1, cCol -1 );
                    if (myBoard.getPiece(next) != null){
                        if (myBoard.getPiece(next).getTeamColor() != color){
                            if(cRow == 2){
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.KNIGHT));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.BISHOP));
                            } else{
                                moves.add(new ChessMove(myPosition, next, null));
                            }
                        }

                    }
                }

                if (cCol != 8){
                    next = new ChessPosition(cRow - 1, cCol + 1);
                    if (myBoard.getPiece(next) != null){
                        if (myBoard.getPiece(next).getTeamColor() != color){
                            if(cRow == 2){
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.KNIGHT));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, next, ChessPiece.PieceType.BISHOP));
                            } else{
                                moves.add(new ChessMove(myPosition, next, null));
                            }
                        }

                    }
                }


            }

        }

        return moves;
        //throw new RuntimeException("not done");
    }
}
