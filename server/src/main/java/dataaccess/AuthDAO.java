package dataaccess;

import model.AuthData;



public interface AuthDAO {
    AuthData getToken (String token) throws ServerException;

    void addToken(String token, String username) throws ServerException;

    void deleteToken(String token) throws ServerException;

    void clear() throws ServerException;

    AuthData getUser (String user) throws ServerException;

}
