package dataaccess;

import com.google.gson.Gson;
import model.AuthData;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public AuthData getToken(String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT token, username, json FROM auth WHERE token=?";
            try (var ps = conn.prepareStatement(statement)){
                ps.setString(1, token);
                try (var rs = ps.executeQuery()){
                    if (rs.next()){
                        return readToken(rs);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

        return null;
    }



    @Override
    public void addToken(String token, String username) throws DataAccessException {
        var statement = "INSERT INTO auth (token, username, json) values(?, ?, ?)";
        var json = new Gson().toJson(new AuthData(token, username));
        executeUpdate(statement, token, username, json);
    }

    @Override
    public void deleteToken(String token) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE token=?";
        executeUpdate(statement, token);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (var i = 0; i < params.length; i++){
                    var param = params[i];
                    if (param instanceof String p){
                        ps.setString(i+1, p);
                    } else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

            }
        } catch (SQLException e){
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()), 500);
        }
    }

    private AuthData readToken(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }


    private final String[] createStatements = {  //TODO add foreign key line?
            """
                CREATE TABLE IF NOT EXISTS  auth (
                              `token` varchar(256) NOT NULL,
                              `username` varchar(256) NOT NULL,
                              `json` TEXT DEFAULT NULL,
                              PRIMARY KEY (`token`),
                              INDEX(token)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                """
    };

    void configureDatabase() throws DataAccessException {
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
