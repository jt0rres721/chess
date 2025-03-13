package dataaccess;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;
import service.AppService;
import service.GameService;
import service.UserService;

public class dataTests {
    private UserService userService;
    private AppService appService;
    private GameService gameService;
    private UserDAO userData;
    private AuthDAO authData;
    private GameDAO gameData;

    @BeforeEach
    void setUp() throws DataAccessException {
        userData = new SQLUserDAO();//new MemoryUserDAO();
        authData = new MemoryAuthDAO();
        gameData = new MemoryGameDAO();
    }

    @Test
    void configureDb() throws DataAccessException{ // TODO add AuthDAO and GameDAO and test again
        System.out.println("No crashes, check out mysqlsh to see if initial values were set up alr");
    }

    @Test
    void getUserTestPositive(){

    }


    @Test
    void addUserTestPositive() throws DataAccessException{
        userData.addUser("gaylord", "psasword", "emailnigga");
    }

    //@Test addUserTestNegative() throws DataAccessException{}

}
