package dataaccess;

import model.UserData;
import dataaccess.DatabaseManager;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void addUser(String username, String password, String email) {

    }

    @Override
    public void clear() {

    }

    @Override
    public int size() {
        return 0;
    }

    private final String[] createStatements = {
        """
                CREATE TABLE IF NOT EXISTS  pet (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `name` varchar(256) NOT NULL,
                              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
                              `json` TEXT DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              INDEX(type),
                              INDEX(name)
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
