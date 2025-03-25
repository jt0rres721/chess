package dataaccess;

import model.AuthData;



public interface AuthDAO {
    AuthData getToken (String token) throws DataAccessException;

    void addToken(String token, String username) throws DataAccessException;

    void deleteToken(String token) throws DataAccessException;

    void clear() throws DataAccessException;

    AuthData getUser (String user) throws DataAccessException;

}
