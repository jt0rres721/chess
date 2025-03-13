package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class dataTests {
    private UserDAO userData;
    private AuthDAO authData;
    private GameDAO gameData;

    @BeforeEach
    void setUp() throws DataAccessException {
        userData = new SQLUserDAO();//new MemoryUserDAO();
        authData = new SQLAuthDAO();
        gameData = new SQLGameDAO();
    }

    @AfterEach
    void wrapUp() throws DataAccessException {
        userData.clear();
        authData.clear();
        gameData.clear();
    }

    @Test
    void configureDb() throws DataAccessException{
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

    @Test
    void createGamePos() throws DataAccessException{
        GameData game1 = gameData.create("NewMoon");
        GameData game2 = gameData.create("NewMoon2");
        GameData game3 = gameData.create("NewMoon3");

        assertEquals("NewMoon", game1.gameName());
        assertEquals("NewMoon2", game2.gameName());
        assertEquals("NewMoon3", game3.gameName());

        assertNull(game1.whiteUsername());
        assertNull(game1.blackUsername());

    }

    @Test //Same name tests
    void createGameNeg() throws DataAccessException{
        GameData game1 = gameData.create("NewMoon");
        GameData game2 = gameData.create("NewMoon");
        GameData game3 = gameData.create("NewMoon");

        assertEquals(1,game1.gameID());
        assertEquals("NewMoon", game1.gameName());
        assertEquals(2, game2.gameID());
        assertEquals("NewMoon", game2.gameName());
        assertEquals(3, game3.gameID());
        assertEquals("NewMoon", game3.gameName());
    }

    @Test
    void clearGames() throws DataAccessException{
        gameData.create("NewMoon");
        gameData.create("NewMoon2");
        gameData.create("NewMoon3");
        gameData.clear();
    }

    @Test
    void getGamePos() throws DataAccessException{
        gameData.create("NewMoon");
        gameData.create("NewMoon2");

        GameData game = gameData.getGame(1);
        assertEquals(1, game.gameID());
        assertNull(game.whiteUsername());
        assertEquals("NewMoon", game.gameName());

    }

    @Test
    void getGameNeg() throws DataAccessException{
        gameData.create("NewMoon");
        gameData.create("NewMoon2");

        GameData game = gameData.getGame(15);
        assertNull(game);

    }

    @Test
    void gameList() throws DataAccessException{
        ArrayList<GameData> games = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            GameData game = gameData.create("Game" + (i+1));
            games.add(game);
        }
        List<GameData> gamelist = gameData.list();

        for (int i = 0; i < 5; i++){
            assertEquals(games.get(i).gameID(), gamelist.get(i).gameID());
            assertEquals(games.get(i).gameName(), gamelist.get(i).gameName());
            assertEquals(new Gson().toJson(games.get(i).game()), new Gson().toJson(gamelist.get(i).game()));
        }

    }

    @Test
    void emptyGameList() throws DataAccessException{
        List<GameData> gamelist = gameData.list();

        assertTrue(gamelist.isEmpty());

    }

    @Test
    void joinGamePos() throws DataAccessException{}

    @Test
    void joinGameNeg() throws DataAccessException{}


}


