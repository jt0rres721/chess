package dataaccess;

import model.AuthData;

import java.util.HashMap;


public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData getToken(String token) {
        return authTokens.get(token);
    }

    @Override
    public void addToken(String token, String username) {
        AuthData newToken = new AuthData(token, username);
        authTokens.put(token, newToken);
    }

    @Override
    public void deleteToken(String token) {
        authTokens.remove(token);
    }

    @Override
    public void clear() {
        authTokens.clear();
    }

    @Override
    public String getUser(String token) throws ServerException {
        return null;
    }


}
