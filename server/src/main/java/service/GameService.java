package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.CreateResult;
import server.ListResult;
import server.ListResult2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameService {
    private final GameDAO gameData;
    private final AuthDAO authData;

    public GameService(GameDAO gameData, AuthDAO authData) {
        this.gameData = gameData;
        this.authData = authData;
    }

    public ListResult list(String token) throws DataAccessException{
        if(authData.getToken(token) == null){
            throw new DataAccessException("Error: unauthorized", 401);
        } else {
            List<GameData> games = gameData.list();

            ArrayList<ListResult2> gameList = new ArrayList<>();
            for (GameData game : games){
                String white = "";
                if(game.whiteUsername() != null){
                    white = game.whiteUsername();
                }
                String black = "";
                if(game.blackUsername() != null){
                    black = game.blackUsername();
                }

                gameList.add(new ListResult2(game.gameID(), white, black, game.gameName()));
            }
            return new ListResult(gameList);
        }
    }

    public CreateResult createGame(String token, String gameName) throws DataAccessException{
        if (gameName == null) {
            throw new DataAccessException("Error: bad request", 400);
        } else if(authData.getToken(token) == null){
            throw new DataAccessException("Error: unauthorized", 401);
        } else {
            GameData game = gameData.create(gameName);
            return new CreateResult(game.gameID());
        }
    }

    public GameData getGame(int gameID){
        return gameData.getGame(gameID);
    }

    public GameData joinGame(String token, int gameID, String playerColor) throws DataAccessException{
        if (gameID <= 0 || playerColor == null || playerColor.isEmpty() || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) || gameData.getGame(gameID) == null) {
            throw new DataAccessException("Error: bad request", 400);
        }
        if (authData.getToken(token) == null){
            throw new DataAccessException("Error: unauthorized", 401);
        }

        if (playerColor.equals("WHITE")){
            if (getGame(gameID).whiteUsername() != null){
                throw new DataAccessException("Error: already taken", 403);
            }
        }
        if (playerColor.equals("BLACK")){
            if (getGame(gameID).blackUsername() != null){
                throw new DataAccessException("Error: already taken", 403);
            }
        }

        String username = authData.getToken(token).username();

        /*if (Objects.equals(getGame(gameID).whiteUsername(), username) || Objects.equals(getGame(gameID).blackUsername(), username)){
            throw new DataAccessException("Error: already taken", 403);
        }*/

        GameData result = gameData.joinGame(gameID, playerColor, username);

        return result;
    }


}
