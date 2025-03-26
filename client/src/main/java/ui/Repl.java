package ui;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;



public class Repl {
    private final Client client;

    public Repl(String serverUrl){
        client = new Client(serverUrl, this);
    }

    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to 240 Chess. Type help to get started");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit") ||!result.equals("q")){
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
        System.out.println("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
    }


}
