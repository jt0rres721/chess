package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getToken (String token);

    void addToken(String token, String username);

    void deleteToken(String token);

    void clear();

}
