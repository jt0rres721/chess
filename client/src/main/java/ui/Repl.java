package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.websocket.NotificationHandler;
import websocket.messages.ServerMessage;


public class Repl implements NotificationHandler{
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl, this);
    }

    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to 240 Chess. Type help to get started");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try{
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }

        System.out.println();
    }

    private void printPrompt(){
        System.out.println("\n" + RESET_TEXT_COLOR +"[" +client.state()+"]" +" >>> " + SET_TEXT_COLOR_GREEN);
    }


    @Override
    public void notify(ServerMessage message) {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
            System.out.println(SET_TEXT_COLOR_RED + message.getMessage());
            printPrompt();
        }
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
            client.addGame(new Gson().fromJson(message.getGame(), ChessGame.class));
            System.out.println(client.printBoard(null));
            printPrompt();
        }
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            System.out.println(SET_TEXT_COLOR_RED + message.getError());
            printPrompt();
        }

    }
}
