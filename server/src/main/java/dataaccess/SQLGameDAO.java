package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.sql.Types.NULL;
import static dataaccess.DatabaseManager.configureDatabase;


public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws ServerException {
        String[] createStatements = {
                """
                CREATE TABLE IF NOT EXISTS  games (
                              `gameID` int NOT NULL AUTO_INCREMENT,
                              `whiteUsername` varchar(256),
                              `blackUsername` varchar(256),
                              `gameName` varchar(256) NOT NULL,
                              `game` TEXT DEFAULT NULL,
                              `json` TEXT DEFAULT NULL,
                              PRIMARY KEY (`gameID`),
                              INDEX(gameName)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                """
        };
        configureDatabase(createStatements);
    }

    @Override
    public GameData create(String gameName) throws ServerException {
        var statement = "INSERT INTO games(gameName, game) values(?, ?)";
        ChessGame game = new ChessGame();
        var jgame = new Gson().toJson(game);
        var id = executeUpdate(statement, gameName, jgame);

        var statement2 = "UPDATE games SET json = ? WHERE gameID = ?";
        var json = new Gson().toJson(new GameData(id, null, null, gameName, game));
        executeUpdate(statement2, json, id);

        return getGame(id);
    }

    @Override
    public void clear() throws ServerException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public List<GameData> list() throws ServerException {
        ArrayList<GameData> games = new ArrayList<>();
        int rows = 0;
        var statement = "SELECT COUNT(*) AS rowCount FROM games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        rows = rs.getInt("rowCount");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServerException(String.format("Unable to get table size: %s", e.getMessage()), 500);
        }

        for (int i = 0; i < rows; i++){
            GameData game = getGame(i+1);
            games.add(game);
        }
        return games;
    }

    @Override
    public GameData getGame(int gameID) throws ServerException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameid, json FROM games WHERE gameid=?";
            try (var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()){
                    if (rs.next()){
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e){
            throw new ServerException(String.format("Unable to read data from gameData: %s", e.getMessage()), 500);
        }

        return null;
    }

    @Override
    public ChessGame getChess(int gameID) throws ServerException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameid, game FROM games WHERE gameid=?";
            try (var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()){
                    if (rs.next()){
                        return new Gson().fromJson(rs.getString("game"), ChessGame.class);
                    }
                }
            }
        } catch (Exception e){
            throw new ServerException(String.format("Unable to read data from chess file: %s", e.getMessage()), 500);
        }

        return null;
    }

    @Override
    public GameData joinGame(int gameID, String playerColor, String username) throws ServerException {
        var statement = "";
        if(Objects.equals(playerColor, "WHITE")&& getGame(gameID)!= null){
            statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
            executeUpdate(statement, username, gameID);

            GameData game1 = getGame(gameID);

            var statement2 = "UPDATE games SET json = ? WHERE gameID = ?";
            var json = new Gson().toJson(new GameData(gameID, username,
                    game1.blackUsername(),game1.gameName(), game1.game() ));
            executeUpdate(statement2, json, gameID);

            return getGame(gameID);

        }
        else if(Objects.equals(playerColor, "BLACK")&& getGame(gameID)!= null){
            statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
            executeUpdate(statement, username, gameID);

            GameData game1 = getGame(gameID);

            var statement2 = "UPDATE games SET json = ? WHERE gameID = ?";
            var json = new Gson().toJson(new GameData(gameID, game1.whiteUsername(),
                    username,game1.gameName(), game1.game() ));
            executeUpdate(statement2, json, gameID);

            return getGame(gameID);
        }
        return null;
    }

    private int executeUpdate(String statement, Object... params) throws ServerException {
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (var i = 0; i < params.length; i++){
                    var param = params[i];
                    if (param instanceof String p){
                        ps.setString(i+1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                    else if (param instanceof ChessGame p){
                        ps.setString(i+1, p.toString());
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e){
            throw new ServerException(String.format("unable to update database: %s, %s", statement, e.getMessage()), 500);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, GameData.class);
    }

    @Override
    public void makeMove(ChessMove move, int gameID) throws ServerException {
        var game = getChess(gameID);
        System.out.println("Successfully pulled chess game in make move");
        try{
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new ServerException("Error: Invalid move, " + e.getMessage(), 500);
        }

        var statement = "UPDATE games SET game = ? WHERE gameID = ?";
        var jgame = new Gson().toJson(game);
        executeUpdate(statement, jgame,gameID);

        GameData game2 = getGame(gameID);
        var second = "UPDATE games SET json = ? WHERE gameID = ?";
        var json = new Gson().toJson(new GameData(game2.gameID(), game2.whiteUsername(), game2.blackUsername(),
                game2.gameName(), game2.game()));
        executeUpdate(second, json, gameID);

    }


}
