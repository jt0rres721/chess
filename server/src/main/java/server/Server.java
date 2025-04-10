package server;

import dataaccess.*;
import dataaccess.ServerException;
import model.*;
import server.websocket.WebSocketHandler;
import service.AppService;
import service.GameService;
import spark.*;
import com.google.gson.Gson;
import service.UserService;

public class Server {
    private final UserService userService;
    private final AppService appService;
    private final GameService gameService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        UserDAO userData = null;
        try {
            userData = new SQLUserDAO();
        } catch (ServerException e) {
            System.out.println("UserData exception caught");
        }
        AuthDAO authData = null;
        try {
            authData = new SQLAuthDAO();
        } catch (ServerException e) {
            System.out.println("AuthData exception caught");
        }
        GameDAO gameData = null;
        try {
            gameData = new SQLGameDAO();
        } catch (ServerException e) {
            System.out.println("GameData exception caught");
        }

        this.userService = new UserService(userData, authData);
        this.appService = new AppService(gameData, authData, userData);
        this.gameService = new GameService(gameData, authData);

        webSocketHandler = new WebSocketHandler(this.userService, this.gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.delete("/db", this::clear);

        Spark.exception(ServerException.class, this::exceptionHandler);



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ServerException ex, Request req, Response res){
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }


    private Object register(Request req, Response res) throws ServerException {
        var body = new Gson().fromJson(req.body(), RegisterRequest.class);

        if (body.username() == null || body.password() == null || body.email() == null){
            throw new ServerException("Error: bad request", 400);
        }

        RegisterResult result = userService.register(body.username(), body.password(), body.email());
        //System.out.println(result.toString());

        return new Gson().toJson(result);
    }

    private Object login(Request req, Response res) throws ServerException {
        var body = new Gson().fromJson(req.body(), LoginRequest.class);

        if (body.username() == null || body.password() == null){
            throw new ServerException("Error: bad request", 400);
        }

        LoginResult result = userService.login(body.username(), body.password());

        return new Gson().toJson(result);

    }

    private Object logout(Request req, Response res) throws ServerException {
        String token = req.headers("Authorization");

        userService.logout(token);

        return new Gson().toJson(null);
    }

    private Object clear(Request req, Response res) throws ServerException {
        appService.clear();

        return new Gson().toJson(null);
    }

    private Object listGames(Request req, Response res) throws ServerException {
        String token = req.headers("Authorization");

        ListResult result = gameService.list(token);

        return new Gson().toJson(result);
    }

    private Object createGame(Request req, Response res) throws ServerException {
        String token = req.headers("Authorization");
        var body = new Gson().fromJson(req.body(), CreateRequest.class);


        CreateResult result = gameService.createGame(token, body.gameName());

        return new Gson().toJson(result);
    }

    private Object joinGame(Request req, Response res) throws ServerException {
        String token = req.headers("Authorization");

        JoinRequest body;
        try {
            body = new Gson().fromJson(req.body(), JoinRequest.class);
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new ServerException("Error: bad request", 400);
        }



        gameService.joinGame(token, body.gameID(), body.playerColor());

        return new Gson().toJson(null);
    }

}
