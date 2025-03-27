package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser (String username) throws ServerException;

    void addUser(String username, String password, String email) throws ServerException;

    void clear() throws ServerException;

    boolean verifyUser(String username, String password) throws ServerException;
}



