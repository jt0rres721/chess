package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

import java.util.List;

public interface GameDAO {

    GameData create(String gameName) throws ServerException;

    void clear() throws ServerException;

    List<GameData> list() throws ServerException;

    GameData getGame(int gameID) throws ServerException;

    GameData joinGame(int gameID, String playerColor, String username) throws ServerException;

    ChessGame getChess(int gameID)throws ServerException;

    void updateGame(ChessGame game, int gameID) throws ServerException;
}
