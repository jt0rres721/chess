package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.DataAccessException;
import model.*;
import server.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.HashMap;

public class Client {
    private String authToken = "";
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private HashMap<Integer, ListResult2> games = new HashMap<>();
    private String color = "";


    public Client(String serverUrl){
        server = new ServerFacade(serverUrl);
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
            case "print" -> printBoard(color);
            case "leave" -> leaveGame();
            case "quit" -> "quit";
            default -> helpG();
        };
    }

    private String leaveGame(){
        state = State.SIGNEDIN;
        return "Left game";
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
            int id;
            try{
                id = Integer.parseInt(params[0]);
            } catch (Exception e){
                throw new DataAccessException("Error: Bad request", 400);
            }

            JoinRequest join = new JoinRequest(params[1].toUpperCase(), id);
            server.joinGame(join, authToken);
            state = State.GAMING;
            color = params[1];


            return printBoard(params[1]);
        } throw new DataAccessException("Error: Bad request", 400);
    }

    private String observe(String... params) throws DataAccessException {
        if (params.length >= 1){
            return printBoard("white");
        } throw new DataAccessException("Error: Bad request", 400);
    }





    public void setState(State st){
        state = st;
    }

    private String printBoard(String color){
        ChessBoard board = new ChessBoard();//game.getBoard();
        board.resetBoard();


        StringBuilder output = new StringBuilder();
        if (color.equals( "white")) {
            output.append(SET_TEXT_COLOR_BLACK);
            output.append(printHeader(color));

            for (int i = 0; i < 8; i++) {
                output.append(SET_BG_COLOR_LIGHT_GREY).append(String.format(" %d ", 8 - i));
                for (int j = 0; j < 8; j++) {
                    ChessPosition position = new ChessPosition(8-i,j+1);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        output.append(printSquare(8-i, j+1));
                    } else {
                        output.append(printPiece(piece, 8 - i, j + 1));
                    }



                }
                output.append(SET_BG_COLOR_LIGHT_GREY).append(String.format(" %d ", 8 - i));
                output.append(RESET_BG_COLOR + "\n");
            }
            //output.append(SET_TEXT_COLOR_BLACK);
            output.append(printHeader(color));

        } else {
            output.append(SET_TEXT_COLOR_BLACK);
            output.append(printHeader(color));

            for (int i = 0; i < 8; i++) {
                output.append(SET_BG_COLOR_LIGHT_GREY).append(String.format(" %d ", i + 1));
                for (int j = 0; j < 8; j++) {
                    ChessPosition position = new ChessPosition(i+1,8-j);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        output.append(printSquare(i+1, 8-j));
                    } else {
                        output.append(printPiece(piece, i+1, 8-j));
                    }



                }
                output.append(SET_BG_COLOR_LIGHT_GREY).append(String.format(" %d ", i + 1));
                output.append(RESET_BG_COLOR + "\n");
            }
            output.append(SET_TEXT_COLOR_BLACK);
            output.append(printHeader(color));
        }


        output.append(RESET_BG_COLOR);
        return output.toString();
    }

    private String printSquare(int row, int col){
        String square;
        if (row % 2 == 0){
            if (col % 2 == 0){
                square = SET_BG_COLOR_DARK_BROWN + EMPTY;
            } else {
                square = SET_BG_COLOR_LIGHT_BROWN + EMPTY;
            }
        } else {
            if (col % 2 == 0){
                square = SET_BG_COLOR_LIGHT_BROWN + EMPTY;
            } else {
                square = SET_BG_COLOR_DARK_BROWN + EMPTY;
            }
        }
        return square;
    }

    private String printPiece(ChessPiece piece, int row, int col){
        String square;
        String pieceString;
        switch (piece.getPieceType()){
            case KNIGHT -> pieceString = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case BISHOP -> pieceString = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> pieceString = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case QUEEN -> pieceString = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            case KING -> pieceString = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            default -> pieceString = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
        }
        if (row % 2 == 0){
            if (col % 2 == 0){
                square = SET_BG_COLOR_DARK_BROWN + pieceString;
            } else {
                square = SET_BG_COLOR_LIGHT_BROWN + pieceString;
            }
        } else {
            if (col % 2 == 0){
                square = SET_BG_COLOR_LIGHT_BROWN + pieceString;
            } else {
                square = SET_BG_COLOR_DARK_BROWN + pieceString;
            }
        }
        return square;
    }

    private String printHeader(String color){
        StringBuilder output = new StringBuilder();
        if (color.equals("white")){
            output.append(SET_BG_COLOR_LIGHT_GREY + EMPTY);
            output.append(SET_BG_COLOR_LIGHT_GREY + " a  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "b  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " c  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " d  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "e  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " f  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "g  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " h ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "   ");
            output.append(RESET_BG_COLOR + "\n");
        } else {
            output.append(SET_BG_COLOR_LIGHT_GREY + EMPTY);
            output.append(SET_BG_COLOR_LIGHT_GREY + " h  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "g  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " f  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " e  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "d  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " c  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "b  ");
            output.append(SET_BG_COLOR_LIGHT_GREY + " a ");
            output.append(SET_BG_COLOR_LIGHT_GREY + "   ");
            output.append(RESET_BG_COLOR + "\n");
        }

        return output.toString();
    }

}
