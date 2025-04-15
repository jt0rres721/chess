package ui;
import chess.*;
import model.*;
import server.ServerException;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import static ui.EscapeSequences.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class Client {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final HashMap<Integer, ListResult2> games = new HashMap<>();
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;
    private final String serverUrl;
    private int currentGameID;
    private String color;
    private String authToken;
    private ChessGame game;

    public Client(String serverUrl, NotificationHandler notificationHandler){
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;

        color = "";
        authToken = "";
        currentGameID = -1;
        game = null;
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
                case RESIGN -> resignPrompt(cmd, params);
                case OBSERVING -> observingClient(cmd);
            };
        }catch (Exception ex){
            return ex.getMessage();
        }
    }
    private String signedOutClient(String cmd, String... params) throws ServerException {
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }
    public String help(){
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
    private String register(String... params) throws ServerException {
        if (params.length == 3){
            RegisterRequest register = new RegisterRequest(params[0], params[1], params[2]);
            var user = server.register(register);
            authToken = user.authToken();

            state = State.SIGNEDIN;

            return String.format("Registered as %s.", user.username());
        } throw new ServerException("Error: Bad request", 400);
    }
    private String login(String... params) throws ServerException{
        if (params.length == 2){
            LoginRequest login = new LoginRequest(params[0], params[1]);
            var user = server.login(login);
            authToken = user.authToken();

            state = State.SIGNEDIN;

            list();

            return String.format("Logged in as %s.", user.username());
        } throw new ServerException("Error: Bad request", 400);
    }
    private String signedInClient(String cmd, String... params) throws ServerException{
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
    private String create(String... params) throws ServerException {
        if (params.length >= 1){
            CreateRequest create = new CreateRequest(params[0]);
            server.createGame(create, authToken);

            list();

            return String.format("Created game called %s", params[0]);
        } throw new ServerException("Error: Bad request", 400);
    }
    private String list() throws ServerException {
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
    private String join(String... params) throws ServerException {
        if (params.length == 2){
            int id;
            try{
                id = Integer.parseInt(params[0]);
            } catch (Exception e){
                throw new ServerException("Error: Bad request", 400);
            }
            int gameID;
            try {
                gameID = games.get(id).gameID();
            } catch (Exception e){
                throw new ServerException("Error: No such game", 400);
            }
            JoinRequest join = new JoinRequest(params[1].toUpperCase(), gameID);
            server.joinGame(join, authToken);

            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.connect(authToken,gameID);
            state = State.GAMING;
            color = params[1];
            currentGameID = gameID;

            return "Joined game as " + color;
        } throw new ServerException("Error: Bad request", 400);
    }
    private String observe(String... params) throws ServerException {
        if (params.length == 1){
            int id;
            try{
                id = Integer.parseInt(params[0]);
            } catch (Exception e){
                throw new ServerException("Error: Bad request", 400);
            }
            int gameID = games.get(id).gameID();

            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.connect(authToken, gameID);
            state = State.GAMING;
            color = "observer";
            currentGameID = gameID;

            state = State.OBSERVING;

            return "Joined game as an observer";


        } throw new ServerException("Error: Bad request", 400);
    }
    private String logout() throws ServerException {
        server.logout(authToken);
        authToken = "";
        state = State.SIGNEDOUT;

        return "Logged out";

    }
    private String logoutAndQuit() throws ServerException{
        server.logout(authToken);
        authToken = "";
        state = State.SIGNEDOUT;

        return "quit";
    }


    private String gamingClient(String cmd, String... params) throws ServerException {
        return switch (cmd) {
            case "redraw" -> printBoard(null);
            case "leave" -> leaveGame();
            case "move" -> makeMove(params);
            case "resign" -> resign();
            case "highlight" -> highlightMoves(params);
            default -> helpG();
        };
    }
    public String helpG() {
        return String.format("""
                %s- redraw %s - to redraw the board
                %s- leave %s - to leave the game
                %s- move <START POSITION> <END POSITION> %s - to make a move  (i.e. a1 a2)
                %s- resign %s -  to forfeit the game
                %s- highlight <POSITION> %s - to highlight the moves of a piece
                %s- help %s - to display possible commands
                """, EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.SET_TEXT_COLOR_MAGENTA);
    }
    private String leaveGame() throws ServerException {
        ws.leave(authToken, currentGameID);
        ws = null;
        currentGameID = -1;
        state = State.SIGNEDIN;
        game = null;
        return "Left game";
    }
    private String makeMove(String... params) throws ServerException {
        if(game.getState() == GameStatus.OVER){
            return "Game has ended. No more moves allowed";
        }
        if(!game.getTeamTurn().toString().toLowerCase().equals(color)){
            throw new ServerException("Error: Bad request, not your turn", 400);
        }
        if (authToken.isEmpty() || currentGameID < 0){
            throw new ServerException("ERROR: Bad Request, no game id or authdata", 400);
        }


        if(params.length != 2 ){
            throw new ServerException("Error: bad request", 400);
        }

        if(params[0].length() != 2 || params[1].length() != 2){
            throw new ServerException("Error: bad request, enter <COLUMN LETTER><ROW NUMBER>", 400);
        }
        String start = params[0];
        String end = params[1];

        ChessPosition startPos = toPosition(start);
        ChessPosition endPos = toPosition(end);

        ChessMove move = new ChessMove(startPos, endPos, null);
        ws.makeMove(authToken, currentGameID, move);
        return "Made move from " + start +" to " + end;
    }
    private String resign(){
        state = State.RESIGN;
        return "This action will result in forfeit of the game. Are you sure? [yes|no]";
    }
    private String highlightMoves(String... params) throws ServerException {
        if (params.length != 1){
            throw new ServerException("Error: bad request", 400);
        }
        ChessPosition position = toPosition(params[0]);
        return printBoard(position);
    }
    private String resignPrompt(String cmd, String... params) throws ServerException {
        return switch(cmd){
            case "yes" -> forfeitGame();
            case "no" -> noForfeit();
            default -> "This action will result in forfeit of the game. Are you sure? [yes|no]";
        };
    }
    private String forfeitGame() throws ServerException {
        ws.resign(authToken, currentGameID);
        state = State.GAMING;
        return "Resigned from the game";
    }
    private String noForfeit(){
        state = State.GAMING;
        return "Continue playing.";
    }

    private String observingClient(String cmd) throws ServerException {
        if(cmd.equals("leave")){
            return leaveGame();
        } else {
            return "Enter 'leave' to leave game";
        }
    }
    private ChessPosition toPosition(String notation) {
        int col = notation.charAt(0) - 'a' + 1;
        int row = Integer.parseInt(notation.substring(1));  // handles ranks 1–8, and even 10 if needed
        return new ChessPosition(row, col);
    }
    public String state(){
        return state.toString();
    }
    public void addGame(ChessGame game){
        this.game = game;
    }

    public String printBoard(ChessPosition highlight){
        if (game == null){
            return "No game";
        }
        ChessBoard board = game.getBoard();

        //Highlight piece shenanigans
        boolean highlighting = false;
        ChessPiece lightPiece;
        Collection<ChessMove> lightMoves;
        Collection<ChessPosition> lightEndPositions = new ArrayList<>();

        if(highlight != null){
            if (board.getPiece(highlight)==null){
                return "Error: no such piece";
            }
            highlighting = true;
            lightPiece = board.getPiece(highlight);
            lightMoves = lightPiece.pieceMoves(board, highlight);
            for (ChessMove mv : lightMoves){
                lightEndPositions.add(mv.getEndPosition());
            }
        }

        boolean lightSquare = false;
        StringBuilder output = new StringBuilder();
        if (color.equals( "white") || color.equals("observer")) {
            output.append(SET_TEXT_COLOR_BLACK);
            output.append(printHeader(color));

            for (int i = 0; i < 8; i++) {
                output.append(SET_BG_COLOR_LIGHT_GREY).append(String.format(" %d ", 8 - i));
                for (int j = 0; j < 8; j++) {
                    ChessPosition position = new ChessPosition(8-i,j+1);
                    ChessPiece piece = board.getPiece(position);

                    if (highlighting){
                        if (lightEndPositions.contains(position)){
                            lightSquare = true;
                        }
                    }

                    if (piece == null){
                        output.append(printSquare(8-i, j+1, lightSquare));
                    } else {
                        output.append(printPiece(piece, 8 - i, j + 1, lightSquare));
                    }
                    lightSquare = false;
                }
                output.append(SET_BG_COLOR_LIGHT_GREY).append(String.format(" %d ", 8 - i));
                output.append(RESET_BG_COLOR + "\n");
            }
            output.append(printHeader(color));

        } else {
            output.append(SET_TEXT_COLOR_BLACK);
            output.append(printHeader(color));

            for (int i = 0; i < 8; i++) {
                output.append(SET_BG_COLOR_LIGHT_GREY).append(String.format(" %d ", i + 1));
                for (int j = 0; j < 8; j++) {
                    ChessPosition position = new ChessPosition(i+1,8-j);
                    ChessPiece piece = board.getPiece(position);

                    if (highlighting){
                        if (lightEndPositions.contains(position)){
                            lightSquare = true;
                        }
                    }
                    if (piece == null){
                        output.append(printSquare(i+1, 8-j, lightSquare));
                    } else {
                        output.append(printPiece(piece, i+1, 8-j, lightSquare));
                    }
                    lightSquare = false;
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

    private String printSquare(int row, int col, boolean highlight){
        String square;

        if (row % 2 == 0){
            if (col % 2 == 0){
                square = highlight ? SET_BG_COLOR_GREEN + EMPTY : SET_BG_COLOR_DARK_BROWN + EMPTY;
            } else {
                square = highlight ? SET_BG_COLOR_YELLOW + EMPTY : SET_BG_COLOR_LIGHT_BROWN + EMPTY;
            }
        } else {
            if (col % 2 == 0){
                square = highlight ? SET_BG_COLOR_YELLOW + EMPTY : SET_BG_COLOR_LIGHT_BROWN + EMPTY;
            } else {
                square = highlight ? SET_BG_COLOR_GREEN + EMPTY : SET_BG_COLOR_DARK_BROWN + EMPTY;
            }
        }
        return square;
    }

    private String printPiece(ChessPiece piece, int row, int col, boolean highlight){
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
        String square1 = highlight ? SET_BG_COLOR_GREEN + pieceString : SET_BG_COLOR_DARK_BROWN + pieceString;
        String square2 = highlight ? SET_BG_COLOR_YELLOW + pieceString : SET_BG_COLOR_LIGHT_BROWN + pieceString;
        if (row % 2 == 0){
            if (col % 2 == 0){
                square = square1;
            } else {
                square = square2;
            }
        } else {
            if (col % 2 == 0){
                square = square2;
            } else {
                square = square1;
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
