package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    GameData create(String gameName) throws DataAccessException;

    void clear() throws DataAccessException;

    List<GameData> list() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData joinGame(int gameID, String playerColor, String username);

}
