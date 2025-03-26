package ui;

import server.ServerFacade;

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

    public String eval(String line){
        return "";
    }

    public String help(){ //TODO Implement
        return "";
    }

}
