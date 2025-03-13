package dataaccess;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;
import service.AppService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


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

    @AfterEach
    void wrapUp() throws DataAccessException {
        userData.clear();
    }

    @Test
    void configureDb() throws DataAccessException{ // TODO add AuthDAO and GameDAO and test again
        System.out.println("No crashes, check out mysqlsh to see if initial values were set up alr");
    }

    @Test
    void getUserTestPositive() throws DataAccessException {
        userData.addUser("user1", "x", "x");
        UserData user = userData.getUser("user1");
        assertEquals("x", user.email());
        assertEquals("x", user.password());
        assertEquals("user1", user.username());
    }

    @Test
    void getUserTestNegative() throws DataAccessException{
        //Testing for a non-existent user
        UserData user = userData.getUser("nonexistentUser");

        assertNull(user);
    }



    @Test
     void addUserTestPositive() throws DataAccessException{
        userData.addUser("user2221", "psasword", "user.gmail");
        UserData user = userData.getUser("user2221");

        assertEquals("user2221", user.username());
        assertEquals("psasword", user.password());
        assertEquals("user.gmail", user.email());

    }

    @Test // Tests for adding an existing user
    void addUserTestNegative() throws DataAccessException{
        userData.addUser("userx", "password", "email");

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                userData.addUser("userx", "password", "email"));

        assertEquals("unable to update database: INSERT INTO users (username, password, email, json)" +
                " values(?, ?, ?, ?), Duplicate entry 'userx' for key 'users.PRIMARY'", exception.getMessage());
    }

    @Test
    void clearUsers() throws DataAccessException{
        userData.addUser("1", "x" , "email");
        userData.clear();

        UserData user = userData.getUser("1");

        assertNull(user);
    }

}


