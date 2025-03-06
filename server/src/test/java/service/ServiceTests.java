package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private UserService userService;
    private AppService appService;
    private GameService gameService;
    private UserDAO userData;
    private AuthDAO authData;
    private GameDAO gameData;

    @BeforeEach
    void setUp() {
        userData = new MemoryUserDAO();
        authData = new MemoryAuthDAO();
        gameData = new MemoryGameDAO();
        userService = new UserService(userData, authData);
        appService = new AppService(gameData, authData, userData);
        gameService = new GameService(gameData, authData);
    }

    // Positive Test
    @Test
    void testRegisterSuccess() throws DataAccessException {
        RegisterResult result = userService.register("Alice", "securePass", "alice@example.com");

        assertNotNull(result);
        assertEquals("Alice", result.username());


        UserData storedUser = userService.getUser("Alice");
        assertNotNull(storedUser);
        assertEquals("Alice", storedUser.username());
        assertEquals("securePass", storedUser.password());
        assertEquals("alice@example.com", storedUser.email());

        AuthData storedAuth = userService.getToken(result.authToken());
        assertEquals("Alice", storedAuth.username());


    }

    // Negative Test
    @Test
    void testRegisterDuplicateUsername() throws DataAccessException {
        userService.register("Bob", "password123", "bob@example.com");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register("Bob", "newPassword", "newbob@example.com");
        });

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.StatusCode());
    }


    //positive login test
    @Test
    void testLogin() throws DataAccessException {
        this.userData.addUser("Jim", "pass", "email");

        LoginResult result = userService.login("Jim", "pass");
        assertNotNull(result);
        assertEquals("Jim", result.username());
        assertNotNull(result.authToken());


    }

    // negative login test
    @Test
    void testLoginWrongPasswordAndUser() throws DataAccessException{
        this.userData.addUser("Jim", "pass", "email");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("Bob", "newPassword");
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());

        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> {
            userService.login("Jim", "newPassword");
        });

        assertEquals("Error: unauthorized", exception2.getMessage());
        assertEquals(401, exception2.StatusCode());



    }

    //positive logout test
    @Test
    void testLogout() throws DataAccessException {
        this.userData.addUser("Jim", "pass", "email");

        LoginResult result = userService.login("Jim", "pass");
        assertNotNull(result);
        assertEquals("Jim", result.username());
        assertNotNull(result.authToken());

        String token = result.authToken();

        userService.logout(token);
        assertNull(this.authData.getToken(token));

    }


    //negative logout test
    @Test
    void testLogoutNegative() throws DataAccessException{
        this.userData.addUser("Jim", "pass", "email");

        LoginResult result = userService.login("Jim", "pass");
        assertNotNull(result);
        assertEquals("Jim", result.username());
        assertNotNull(result.authToken());

        String token = result.authToken();

        //logout wrong token
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.logout("bilbobaggins");
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());

        //logout twice
        userService.logout(token);
        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> {
            userService.logout(token);
        });

        assertEquals("Error: unauthorized", exception2.getMessage());
        assertEquals(401, exception2.StatusCode());



    }


    //Positive clear tests
    @Test
    void testClearApp() throws DataAccessException {
        userService.register("Alice", "securePass", "alice@example.com");
        userService.register("Alce", "securePass", "alice@example.com");
        userService.register("lice", "securePass", "alice@example.com");
        userService.register("Alike", "securePass", "alice@example.com");
        userService.register("Alie", "securePass", "alice@example.com");


        assertEquals(5, userData.size());

        appService.clear();

        assertEquals(0, userData.size());

    }


    @Test
    void testCreateGame() throws DataAccessException {
        RegisterResult result = userService.register("Alice", "securePass", "alice@example.com");
        assertNotNull(result);
        assertEquals("Alice", result.username());

        String token = result.authToken();
        CreateResult r = gameService.createGame(token, "GGame");

        GameData storedGame = gameService.getGame(r.gameID());
        assertNotNull(storedGame);
        assertEquals("GGame", storedGame.gameName());
        assertNull(storedGame.whiteUsername());
        assertNull(storedGame.blackUsername());

    }

    @Test
    void testCreateGameNegative() throws DataAccessException {
        RegisterResult result = userService.register("Alice", "securePass", "alice@example.com");
        assertNotNull(result);
        assertEquals("Alice", result.username());

        String token = result.authToken();


        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(token, null);
        });

        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.StatusCode());

        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> {
            gameService.createGame("falseToken", "GGame");
        });

        assertEquals("Error: unauthorized", exception2.getMessage());
        assertEquals(401, exception2.StatusCode());



    }

    @Test
    void listAllGamesTestPositive() throws DataAccessException{
        RegisterResult result = userService.register("Alice", "securePass", "alice@example.com");
        assertNotNull(result);
        assertEquals("Alice", result.username());

        String token = result.authToken();
        for (int i = 0 ; i< 5; i++){
            gameService.createGame(token, "GGame");
        }
        ListResult r = gameService.list(token);


        for (ListResult2 game : r.list()) {
            assertNotNull(game);
            assertEquals("GGame", game.gameName());
            assertNull(game.whiteUsername());
            assertNull(game.blackUsername());
        }




    }

    @Test
    void listAllGamesTestNegative() throws DataAccessException{
        RegisterResult result = userService.register("Alice", "securePass", "alice@example.com");
        assertNotNull(result);
        assertEquals("Alice", result.username());

        String token = result.authToken();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.list("invalid token");
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());


    }

    @Test
    void joinPositive() throws DataAccessException {
        GameData game = gameData.create("Game");
        var storedUser = userService.register("bob", "ni", "email");
        String token = storedUser.authToken();

        GameData game1 = gameService.joinGame(token, game.gameID(), "BLACK");

        assertNotNull(game1);
        assertEquals(game.gameID(), game1.gameID());
        assertEquals("bob", game1.blackUsername());
        assertNull(game1.whiteUsername());

    }

    @Test
    void joinNegative() throws DataAccessException {
        GameData game = gameData.create("Game");
        var storedUser = userService.register("bob", "ni", "email");
        String token = storedUser.authToken();

        //tests for bad request:
        //bad gameID
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(token, 500, "WHITE");
        });

        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.StatusCode());

        //bad playerColor
        exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(token, game.gameID(), "WHTE");
        });

        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.StatusCode());


        //Test for unauthorized request
        exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame("invalid token", game.gameID(), "WHITE");
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());



        //Test for already taken username
        gameData.joinGame(game.gameID(), "WHITE", "bobnemesis");
        exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(token, game.gameID(), "WHITE");
        });

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.StatusCode());

        //Test for duplicate username in same game?



    }







}
