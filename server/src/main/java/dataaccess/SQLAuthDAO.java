package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws ServerException {
        String[] createStatements = {
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
        configureDatabase(createStatements);
    }

    @Override
    public AuthData getToken(String token) throws ServerException {
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
            throw new ServerException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

        return null;
    }



    @Override
    public void addToken(String token, String username) throws ServerException {
        var statement = "INSERT INTO auth (token, username, json) values(?, ?, ?)";
        var json = new Gson().toJson(new AuthData(token, username));
        executeUpdate(statement, token, username, json);
    }

    @Override
    public void deleteToken(String token) throws ServerException {
        var statement = "DELETE FROM auth WHERE token=?";
        executeUpdate(statement, token);
    }

    @Override
    public void clear() throws ServerException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public String getUser(String token) throws ServerException {
        AuthData auth = getToken(token);
        return auth.username();
    }


    private AuthData readToken(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }


}
