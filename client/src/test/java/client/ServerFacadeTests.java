package client;

import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import javax.xml.crypto.Data;

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
    static void stopServer() throws DataAccessException {
        facade.clear();
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("luigi", "defund", "depose@gmail");
        var result = facade.register(request);


        assertEquals("luigi", result.username());
    }

    @Test
    public void registerNegTest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request); //Initial registration

        //duplicate registration
        DataAccessException exception = assertThrows(DataAccessException.class, () -> facade.register(request));

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.statusCode());
    }

    @Test
    public void loginTest() throws DataAccessException{
        RegisterRequest request2 = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request2);

        LoginRequest request = new LoginRequest("luigi", "defund");
        var result = facade.login(request);

        assertNotNull(result.authToken());
        assertEquals("luigi", result.username());
    }

    @Test
    public void loginTestNeg()throws DataAccessException{
        RegisterRequest request2 = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request2);

        //test wrong password
        LoginRequest request = new LoginRequest("luigi", "def");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> facade.login(request));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.statusCode());

        //test non-existing user
        LoginRequest request3 = new LoginRequest("mario", "defund");
        DataAccessException ex  =assertThrows(DataAccessException.class, () -> facade.login(request3));

        assertEquals("Error: unauthorized", ex.getMessage());
        assertEquals(401, ex.statusCode());


    }

    @Test
    public void logoutTest() throws DataAccessException{
        RegisterRequest request2 = new RegisterRequest("luigi", "defund", "depose@gmail");
        var user = facade.register(request2);

        facade.logout(user.authToken());
        System.out.println("No exceptions thrown tests successful");
    }

    @Test
    public void logoutNeg() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        //Invalid authToken
        DataAccessException ex  =assertThrows(DataAccessException.class, () -> facade.logout("invalidToken"));

        assertEquals("Error: unauthorized", ex.getMessage());
        assertEquals(401, ex.statusCode());


    }

    //TODO proceed with other tests and facade functions after logout.
    @Test
    public void gameList() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        //TODO create games here once you implement the function

        var games = facade.listGames(user.authToken());

        assertEquals("Game1", games.list().getFirst().gameName());
    }

    @Test
    public void createGame() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("mario", "x", "x@x");
        var user = facade.register(request);

        CreateRequest gameRequest = new CreateRequest("Gameni");

        var game = facade.createGame(gameRequest, user.authToken());

        assertEquals("Gameni", game.gameName());
    }

    @Test
    public void clearTest() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("luigi", "defund", "depose@gmail");
        facade.register(request);

        RegisterRequest request2 = new RegisterRequest("luigi1", "defund", "depose@gmail");
        facade.register(request2);

        facade.clear();
    }

}
