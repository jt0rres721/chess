package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;

public class AppService {
    private final GameDAO gameData;
    private final AuthDAO authData;
    private final UserDAO userData;


    public AppService(GameDAO gameData, AuthDAO authData, UserDAO userData){

        this.gameData = gameData;
        this.authData = authData;
        this.userData = userData;
    }

    public void clear() throws DataAccessException {
        gameData.clear();
        userData.clear();
        authData.clear();

    }
}
