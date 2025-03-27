package client;

import dataaccess.DataAccessException;
import model.RegisterRequest;
import server.Server;
import server.ServerFacade;
import ui.Client;
import ui.Repl;

import org.junit.jupiter.api.*;
import ui.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientTests {
    private static Server server;
    private static int port;
    private static Client client;
    private static ServerFacade facade;

    static Repl repl = new Repl("http://localhost:" + port);

    @BeforeAll
    public static void init(){
        server = new Server();
        port = server.run(0);
        client = new Client("http://localhost:" + port, repl);
        facade = new ServerFacade("http://localhost:" + port);

    }

    @AfterEach
    public void reset() throws DataAccessException {
        facade.clear();
        client.setState(State.SIGNEDOUT);
    }

    @Test
    public void register(){
        String input = "register joe jonas email";

        String result = client.eval(input);

        assertEquals("Registered as joe.", result);
        assertEquals(State.SIGNEDIN.toString(), client.state());
    }

    @Test
    public void registerNeg(){
        String input = "register joe jia";
        String result = client.eval(input);

        assertEquals("Error: Bad request", result);
    }

    @Test
    public void login() throws DataAccessException {
        RegisterRequest request2 = new RegisterRequest("joe", "jonas", "a@gmail");
        facade.register(request2);

        String input = "login joe jonas";

        String result = client.eval(input);

        assertEquals("Logged in as joe.", result);
        assertEquals(State.SIGNEDIN.toString(), client.state());
    }

    @Test
    public void loginNeg() throws DataAccessException {
        RegisterRequest request2 = new RegisterRequest("joe", "jonas", "a@gmail");
        facade.register(request2);

        String input = "login joe a"; //wrong password
        String result = client.eval(input);
        assertEquals("Error: unauthorized", result);

        input = "login joe";
        result = client.eval(input);
        assertEquals("Error: Bad request", result);
    }

//    @Test
//    public void printBoardTest() throws DataAccessException {
//        System.out.print(client.printBoard("black"));
//    }


}
