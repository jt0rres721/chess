package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;


public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException{
        String[] createStatements = {
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
        configureDatabase(createStatements);
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

    @Override
    public void addUser(String username, String password, String email) throws DataAccessException{
        String hashpass = encryptPassword(password);
        var statement = "INSERT INTO users (username, password, email, json) values(?, ?, ?, ?)";
        var json = new Gson().toJson(new UserData(username, hashpass, email));
        executeUpdate(statement, username, hashpass, email, json);
    }

    @Override
    public void clear() throws DataAccessException{
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    public String encryptPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    public boolean verifyUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        return BCrypt.checkpw(password, user.password());
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
    }


}
