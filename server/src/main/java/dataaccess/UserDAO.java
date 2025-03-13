package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser (String username) throws DataAccessException;

    void addUser(String username, String password, String email) throws DataAccessException;

    void clear() throws DataAccessException;

}



