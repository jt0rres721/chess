package client;

import dataaccess.DataAccessException;
import server.Server;
import server.ServerFacade;
import ui.Client;
import ui.Repl;

import org.junit.jupiter.api.*;
import ui.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @Test
    public void register(){
        String input = "register joe jonas email";

        String result = client.eval(input);

        assertEquals("Registered as joe.", result);
        assertEquals(State.SIGNEDIN.toString(), client.state());
    }
}
