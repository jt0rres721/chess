package ui;

import dataaccess.DataAccessException;
import model.LoginRequest;
import model.RegisterRequest;
import server.ServerFacade;

import javax.xml.crypto.Data;
import java.util.Arrays;

public class Client {
    private String authToken = "";
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
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state){
                case SIGNEDOUT -> signedOutClient(cmd, params);
                case SIGNEDIN -> signedInClient(cmd, params);
                case GAMING -> gamingClient(cmd, params);
            };

        }catch (DataAccessException ex){
            return ex.getMessage();
        }

    }

    public String help(){ //TODO Implement
        return "You ran help my nigga.";
    }

    public String helpIn() {
        return "HelpIn";
    }

    public String state(){
        return state.toString();
    }

    private String register(String... params) throws DataAccessException {
        if (params.length >= 3){
            RegisterRequest register = new RegisterRequest(params[0], params[1], params[2]);
            var user = server.register(register);
            authToken = user.authToken();

            state = State.SIGNEDIN;

            return String.format("Registered as %s.", user.username());
        } throw new DataAccessException("Error: Bad request", 400);
    }

    private String login(String... params) throws DataAccessException{
        if (params.length >= 2){
            LoginRequest login = new LoginRequest(params[0], params[1]);
            var user = server.login(login);
            authToken = user.authToken();

            state = State.SIGNEDIN;

            return String.format("Logged in as %s.", user.username());
        } throw new DataAccessException("Error: Bad request", 400);
    }

    private String signedOutClient(String cmd, String... params) throws DataAccessException {
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String signedInClient(String cmd, String... params) throws DataAccessException{
        return switch (cmd) {
            case "logout" -> logout(params);
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "quit" -> "quit";
            default -> helpIn();
        };
    }

    private String gamingClient(String cmd, String... params) throws DataAccessException{
        return "in gaming";
    }

    private String logout(String... params) throws DataAccessException {
        server.logout(authToken);
        authToken = "";
        state = State.SIGNEDOUT;

        return "Logged out";

    }

    private String create(String... params){
        return null;
    }

    private String list(){
        return null;
    }

    private String join(String... params){
        return null;
    }

    private String observe(String... params){
        return null;
    }





    public void setState(State st){
        state = st;
    }

}
