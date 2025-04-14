package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.ServerException;
import dataaccess.GameDAO;
import model.GameData;
import model.CreateResult;
import model.ListResult;
import model.ListResult2;

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

    public ListResult list(String token) throws ServerException {
        if(authData.getToken(token) == null){
            throw new ServerException("Error: unauthorized", 401);
        } else {
            List<GameData> games = gameData.list();

            ArrayList<ListResult2> gameList = new ArrayList<>();
            for (GameData game : games){


                gameList.add(new ListResult2(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            return new ListResult(gameList);
        }
    }

    public CreateResult createGame(String token, String gameName) throws ServerException {
        if (gameName == null) {
            throw new ServerException("Error: bad request", 400);
        } else if(authData.getToken(token) == null){
            throw new ServerException("Error: unauthorized", 401);
        } else {
            GameData game = gameData.create(gameName);
            return new CreateResult(game.gameID());
        }
    }

    public GameData getGame(int gameID) throws ServerException {
        return gameData.getGame(gameID);
    }


    public ChessGame getChess(int gameID) throws ServerException{
        return gameData.getChess(gameID);
    }

    public void makeMove(ChessMove move, int gameID) throws ServerException {
        var game = getChess(gameID);
        try{
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new ServerException("Error: Invalid move, " + e.getMessage(), 500);
        }

        gameData.updateGame(game, gameID);
    }

    public GameData joinGame(String token, int gameID, String playerColor) throws ServerException {
        if (gameID <= 0 || playerColor == null || playerColor.isEmpty() || (!playerColor.equals("WHITE")
                && !playerColor.equals("BLACK")) || gameData.getGame(gameID) == null) {
            throw new ServerException("Error: bad request", 400);
        }
        if (authData.getToken(token) == null){
            throw new ServerException("Error: unauthorized", 401);
        }

        if (playerColor.equals("WHITE")){
            if (getGame(gameID).whiteUsername() != null){
                throw new ServerException("Error: already taken", 403);
            }
        }
        if (playerColor.equals("BLACK")){
            if (getGame(gameID).blackUsername() != null){
                throw new ServerException("Error: already taken", 403);
            }
        }

        String username = authData.getToken(token).username();



        return gameData.joinGame(gameID, playerColor, username);

    }

    public void leaveGame(String username, int gameID) throws ServerException {
        GameData game = getGame(gameID);
        if(Objects.equals(game.whiteUsername(), username)){
            System.out.println("The leaving user matches white username");
            gameData.leaveGame(gameID, "WHITE");
        } else if (Objects.equals(game.blackUsername(), username)){
            System.out.println("The leaving user matches black username");
            gameData.leaveGame(gameID, "BLACK");
        }
        System.out.println("The leaving user matches no username");
    }

    public void endGame(int gameID) throws ServerException {
        var game = getChess(gameID);
        game.endGame();
        gameData.updateGame(game, gameID);
    }


}
