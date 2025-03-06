package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    GameData create(String gameName);

    void clear();

    List<GameData> list();

    GameData getGame(int gameID);
}
