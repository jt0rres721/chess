package ui;

import dataaccess.DataAccessException;
import model.LoginRequest;
import model.RegisterRequest;
import server.ServerFacade;

import javax.xml.crypto.Data;
import java.util.Arrays;

public class Client {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private Repl repl;


    public Client(String serverUrl, Repl repl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public String eval(String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";  // we can use this for different states.
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        }catch (DataAccessException ex){
            return ex.getMessage();
        }

    }

    public String help(){ //TODO Implement
        return "You ran help my nigga. ";
    }

    public String state(){
        return state.toString();
    }

    private String register(String... params) throws DataAccessException {
        if (params.length == 3){
            RegisterRequest register = new RegisterRequest(params[0], params[1], params[2]);
            var user = server.register(register);

            state = State.SIGNEDIN;

            return String.format("Registered as %s.", user.username());
        } throw new DataAccessException("Error: Bad request", 400);
    }

    private String login(String... params) throws DataAccessException{
        if (params.length == 2){
            LoginRequest login = new LoginRequest(params[0], params[1]);
            var user = server.login(login);

            state = State.SIGNEDIN;

            return String.format("Logged in as %s.", user.username());
        } throw new DataAccessException("Error: Bad request", 400);
    }

}
