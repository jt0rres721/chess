package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException {
        configureDb();
    }

    @Override
    public GameData create(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games(gameName, game) values(?, ?)";
        ChessGame game = new ChessGame();
        var jgame = new Gson().toJson(game);
        var id = executeUpdate(statement, gameName, jgame);

        var statement2 = "UPDATE games SET json = ? WHERE gameID = ?";
        var json = new Gson().toJson(new GameData(id, null, null, gameName, game));
        executeUpdate(statement2, json, id);

        return new GameData(id, null, null, gameName, game);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public List<GameData> list() throws DataAccessException{
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
            throw new DataAccessException(String.format("Unable to get table size: %s", e.getMessage()), 500);
        }

        for (int i = 0; i < rows; i++){
            GameData game = getGame(i+1);
            games.add(game);
        }
        return games;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
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
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

        return null;
    }

    @Override
    public GameData joinGame(int gameID, String playerColor, String username) {
        return null;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (var i = 0; i < params.length; i++){
                    var param = params[i];
                    if (param instanceof String p){
                        ps.setString(i+1, p);
                    } else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
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
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()), 500);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("gameid");//TODO change to ID
        var json = rs.getString("json");
        return new Gson().fromJson(json, GameData.class);
    }

    private final String[] createStatements = {
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

    private void configureDb() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : createStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }

        }catch (SQLException ex){
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()),500);
        }
    }
}
