package ui;

import dataaccess.DataAccessException;
import model.*;
import server.ServerFacade;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.HashMap;

public class Client {
    private String authToken = "";
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private Repl repl;
    private HashMap<Integer, ListResult2> games = new HashMap<>();


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
        return String.format("""
                %s- register <USERNAME> <PASSWORD> <EMAIL> %s - to create an account
                %s- login <USERNAME> <PASSWORD> %s - to play chess
                %s- quit %s - to end session
                %s- help %s - to display possible commands
                """, EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA);
    }

    public String helpIn() {
        return String.format("""
                %s- create <NAME> %s - to create a game
                %s- list %s - to list all games
                %s- join <ID> [WHITE|BLACK] %s - to join a game
                %s- observe <ID> %s -  to observe a game
                %s- logout %s - when you are done
                %s- quit %s - to end session
                %s- help %s - to display possible commands
                """, EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA);
    }

    public String helpG() {
        return "HelpG";
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
            case "logout" -> logout();
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "quit" -> logoutAndQuit();
            default -> helpIn();
        };
    }

    private String gamingClient(String cmd, String... params) throws DataAccessException{
        return switch (cmd) {
            case "quit" -> "quit";
            default -> helpG();
        };
    }

    private String logout() throws DataAccessException {
        server.logout(authToken);
        authToken = "";
        state = State.SIGNEDOUT;

        return "Logged out";

    }

    private String logoutAndQuit() throws DataAccessException{
        server.logout(authToken);
        authToken = "";
        state = State.SIGNEDOUT;

        return "quit";
    }

    private String create(String... params) throws DataAccessException {
        if (params.length >= 1){
            CreateRequest create = new CreateRequest(params[0]);
            var game = server.createGame(create, authToken);

            return String.format("Created game called %s", params[0]);
        } throw new DataAccessException("Error: Bad request", 400);
    }

    private String list() throws DataAccessException {
        games.clear();
        var result = server.listGames(authToken);
        var gameList = result.games();

        StringBuilder output = new StringBuilder();

        if (gameList.isEmpty()){
            return "No open games \n";
        } else {

            for (int i = 1; i <= result.list().size(); i++) {
                games.put(i, gameList.get(i - 1));
                String white = (games.get(i).whiteUsername() != null) ? games.get(i).whiteUsername() : "";
                String black = (games.get(i).blackUsername() != null) ? games.get(i).blackUsername() : "";
                output.append(String.format("%d: Name: %s, White: %s, Black: %s \n", i, games.get(i).gameName(),
                        white, black));
            }
        }

        return output.toString();
    }

    private String join(String... params) throws DataAccessException {
        if (params.length >= 2){
            int id = 0;
            try{
                id = Integer.parseInt(params[0]);
            } catch (Exception e){
                throw new DataAccessException("Error: Bad request", 400);
            }

            JoinRequest join = new JoinRequest(params[1].toUpperCase(), games.get(id).gameID());
            server.joinGame(join, authToken);
            state = State.GAMING;

            return "Joined game";
        } throw new DataAccessException("Error: Bad request", 400);
    }

    private String observe(String... params) throws DataAccessException {
        if (params.length >= 1){
            return "Weird request not yet implememnted"; //TODO implememnt this shi
        } throw new DataAccessException("Error: Bad request", 400);
    }





    public void setState(State st){
        state = st;
    }

}
