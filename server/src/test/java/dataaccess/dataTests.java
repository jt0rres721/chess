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
    void configureDb() throws DataAccessException{
        System.out.println("If your shi hasn't crashed ConfigureDatabase was successfull");
    }

}
