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
    private UserDAO userData;
    private AuthDAO authData;
    private GameDAO gameData;

    @BeforeEach
    void setUp() throws DataAccessException {
        userData = new SQLUserDAO();//new MemoryUserDAO();
        authData = new SQLAuthDAO();
        gameData = new MemoryGameDAO();
    }

    @AfterEach
    void wrapUp() throws DataAccessException {
        userData.clear();
        authData.clear();
    }

    @Test
    void configureDb() throws DataAccessException{ // TODO add GameDAO and test again
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


    @Test
    void addAuth() throws DataAccessException{
        authData.addToken("ntokennax", "x");
        AuthData auth = authData.getToken("ntokennax");

        assertEquals("ntokennax", auth.authToken());

    }

    @Test
    void addAuthNeg() throws DataAccessException{
        //token is already existing
        authData.addToken("ntokennax", "x");
        AuthData auth = authData.getToken("ntokennax");

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                authData.addToken("ntokennax", "x"));

        assertEquals("unable to update database: INSERT INTO auth (token, username, json) " +
                "values(?, ?, ?), Duplicate entry 'ntokennax' for key 'auth.PRIMARY'", exception.getMessage());
        assertEquals(500, exception.statusCode());
    }


    @Test
    void clearAuth() throws DataAccessException{
        authData.clear();
    }

    @Test
    void deleteAuth() throws DataAccessException{
        authData.addToken("yo", "x");
        AuthData auth = authData.getToken("yo");

        assertNotNull(auth);

        authData.deleteToken("yo");

        auth = authData.getToken("yo");

        assertNull(auth);
    }

    @Test
    void deleteAuthNegative() throws DataAccessException{
        authData.addToken("yo", "x");
        AuthData auth = authData.getToken("yo");
        assertNotNull(auth);

        authData.deleteToken("x");
    }

    @Test
    void getAuthPos() throws DataAccessException {
        authData.addToken("tok", "x");
        AuthData auth = authData.getToken("tok");

        assertEquals("tok", auth.authToken());
    }


    @Test
    void getAuthNeg() throws DataAccessException{
        authData.addToken("tok", "x");
        AuthData auth = authData.getToken("tiktok");

        assertNull(auth);
    }
}


