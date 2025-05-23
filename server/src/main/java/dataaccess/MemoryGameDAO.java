package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;


    @Override
    public GameData create(String gameName) {
        GameData game = new GameData(nextID++, null, null, gameName, new ChessGame());
        games.put(game.gameID(), game);
        return game;
    }

    @Override
    public void clear() {
        games.clear();
        nextID = 1;
    }

    @Override
    public List<GameData> list() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public GameData joinGame(int gameID, String playerColor, String username) {
        GameData game = games.get(gameID);

        if (playerColor.equals("WHITE")){
            GameData joinedGame = new GameData(gameID, username, game.blackUsername(),game.gameName(), game.game());
            System.out.println("Joined as WHITE. The other username reads as ");
            if(joinedGame.blackUsername() == null){System.out.println("NULL");}
            if(Objects.equals(joinedGame.blackUsername(), "")){System.out.println("empty string");}
            games.remove(gameID);
            games.put(gameID, joinedGame);
            game = joinedGame;
        } else{
            GameData joinedGame = new GameData(gameID, game.whiteUsername(), username ,game.gameName(), game.game());
            games.remove(gameID);
            games.put(gameID, joinedGame);
            game = joinedGame;
        }



        return game;

    }



    @Override
    public ChessGame getChess(int gameID){
        return null;
    }

    @Override
    public void leaveGame(int gameID, String color) {

    }

    @Override
    public void updateGame(ChessGame game, int gameID){

    }




}
