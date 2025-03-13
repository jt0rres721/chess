package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import dataaccess.DatabaseManager;


import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT username, json FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try (var rs = ps.executeQuery()){
                    if (rs.next()){
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

        return null;
    }

    @Override //TODO: add password hash thingy
    public void addUser(String username, String password, String email) throws DataAccessException{
        var statement = "INSERT INTO users (username, password, email, json) values(?, ?, ?, ?)";
        var json = new Gson().toJson(new UserData(username, password, email));
        executeUpdate(statement, username, password, email, json);
    }

    @Override
    public void clear() throws DataAccessException{
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
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

    private final String[] createStatements = {
        """
                CREATE TABLE IF NOT EXISTS  users (
                              `username` varchar(256) NOT NULL,
                              `password` varchar(256) NOT NULL,
                              `email` varchar(256) NOT NULL,
                              `json` TEXT DEFAULT NULL,
                              PRIMARY KEY (`username`),
                              INDEX(username)
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
