package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.SharedException;
import server.ServerFacade;


import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() throws SharedException {
        facade.clear();
        server.stop();
    }

    @AfterEach
    void clearServer() throws SharedException{
        facade.clear();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest() throws SharedException {
        RegisterRequest request = new RegisterRequest("luigi", "defund", "depose@gmail");
        var result = facade.register(request);


        assertEquals("luigi", result.username());
    }

    @Test
    public void registerNegTest() throws SharedException {
        RegisterRequest request = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request); //Initial registration

        //duplicate registration
        SharedException exception = assertThrows(SharedException.class, () -> facade.register(request));

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(500, exception.getCode());
    }

    @Test
    public void loginTest() throws SharedException{
        RegisterRequest request2 = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request2);

        LoginRequest request = new LoginRequest("luigi", "defund");
        var result = facade.login(request);

        assertNotNull(result.authToken());
        assertEquals("luigi", result.username());
    }

    @Test
    public void loginTestNeg()throws SharedException{
        RegisterRequest request2 = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request2);

        //test wrong password
        LoginRequest request = new LoginRequest("luigi", "def");
        SharedException exception = assertThrows(SharedException.class, () -> facade.login(request));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(500, exception.getCode());

        //test non-existing user
        LoginRequest request3 = new LoginRequest("mario", "defund");
        SharedException ex  =assertThrows(SharedException.class, () -> facade.login(request3));

        assertEquals("Error: unauthorized", ex.getMessage());
        assertEquals(500, ex.getCode());


    }

    @Test
    public void logoutTest() throws SharedException{
        RegisterRequest request2 = new RegisterRequest("luigi", "defund", "depose@gmail");
        var user = facade.register(request2);

        facade.logout(user.authToken());
        System.out.println("No exceptions thrown tests successful");
    }

    @Test
    public void logoutNeg() throws SharedException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        facade.register(request);

        //Invalid authToken
        SharedException ex  =assertThrows(SharedException.class, () -> facade.logout("invalidToken"));

        assertEquals("Error: unauthorized", ex.getMessage());


    }

    @Test
    public void gameList() throws SharedException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        facade.createGame(new CreateRequest("Game1"), user.authToken());
        facade.createGame(new CreateRequest("Game2"), user.authToken());
        facade.createGame(new CreateRequest("Game3"), user.authToken());

        var games = facade.listGames(user.authToken());

        assertEquals("Game1", games.list().getFirst().gameName());
        assertEquals(1, games.list().getFirst().gameID());
        assertEquals("Game2", games.list().get(1).gameName());
        assertEquals(2, games.list().get(1).gameID());
        assertEquals("Game3", games.list().get(2).gameName());
        assertEquals(3, games.list().get(2).gameID());

    }

    @Test
    public void gameListNeg() throws SharedException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        facade.createGame(new CreateRequest("Game1"), user.authToken());
        facade.createGame(new CreateRequest("Game2"), user.authToken());
        facade.createGame(new CreateRequest("Game3"), user.authToken());

        SharedException ex  =assertThrows(SharedException.class, () -> facade.logout("invalidToken"));

        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    public void createGame() throws SharedException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        CreateRequest gameRequest = new CreateRequest("Gameni");

        var game = facade.createGame(gameRequest, user.authToken());

        assertEquals(1, game.gameID());
    }

    @Test
    public void createGameNeg() throws SharedException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        facade.register(request);

        SharedException ex  =assertThrows(SharedException.class, () -> facade.logout("invalidToken"));

        assertEquals("Error: unauthorized", ex.getMessage());
        assertEquals(500, ex.getCode());

    }


    @Test
    public void joinGame() throws SharedException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        CreateRequest gameRequest = new CreateRequest("GameF");
        var game = facade.createGame(gameRequest, user.authToken());

        JoinRequest join = new JoinRequest("WHITE", 1);

        facade.joinGame(join, user.authToken());

        var games = facade.listGames(user.authToken());

        assertEquals("GameF", games.list().getFirst().gameName());
        assertEquals("mario", games.list().getFirst().whiteUsername());
        assertNull(games.list().getFirst().blackUsername());
    }

    @Test
    public void joinGameNeg() throws SharedException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        CreateRequest gameRequest = new CreateRequest("GameF");
        var game = facade.createGame(gameRequest, user.authToken());

        //Inexistent color
        JoinRequest join = new JoinRequest("WHIE", 1);

        SharedException ex  =assertThrows(SharedException.class, () -> facade.joinGame(join, user.authToken()));

        assertEquals("Error: bad request", ex.getMessage());
        assertEquals(500, ex.getCode());

        //unauthorized

        JoinRequest join2 = new JoinRequest("WHITE", 1);
        SharedException ex2  =assertThrows(SharedException.class, () -> facade.joinGame(join2, "invalidToken"));

        assertEquals("Error: unauthorized", ex2.getMessage());


    }

    @Test
    public void clearTest() throws SharedException{
        RegisterRequest request = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request);

        RegisterRequest request2 = new RegisterRequest("luigi1", "defund", "depose@gmail");
        facade.register(request2);

        facade.clear();
    }

}
