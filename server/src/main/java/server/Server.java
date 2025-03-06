package server;

import dataaccess.*;
import service.AppService;
import service.GameService;
import spark.*;
import com.google.gson.Gson;
import service.UserService;

public class Server {
    private final UserService userService;
    private final AppService appService;
    private final GameService gameService;

    public Server() {
        UserDAO userData = new MemoryUserDAO();
        AuthDAO authData = new MemoryAuthDAO();
        GameDAO gameData = new MemoryGameDAO();

        this.userService = new UserService(userData, authData);
        this.appService = new AppService(gameData, authData, userData);
        this.gameService = new GameService(gameData, authData);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::Register);
        Spark.post("/session", this::Login);
        Spark.delete("/session", this::Logout);
        Spark.get("/game", this::ListGames);
        Spark.post("/game", this::CreateGame);
        Spark.put("/game", this::JoinGame);

        Spark.delete("/db", this::Clear);

        Spark.exception(DataAccessException.class, this::exceptionHandler);



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res){
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }


    private Object Register(Request req, Response res) throws DataAccessException {
        var body = new Gson().fromJson(req.body(), RegisterRequest.class);

        if (body.username() == null || body.password() == null || body.email() == null){
            throw new DataAccessException("Error: bad request", 400);
        }

        RegisterResult result = userService.register(body.username(), body.password(), body.email());
        //System.out.println(result.toString());

        return new Gson().toJson(result);
    }

    private Object Login(Request req, Response res) throws DataAccessException {
        var body = new Gson().fromJson(req.body(), LoginRequest.class);

        if (body.username() == null || body.password() == null){
            throw new DataAccessException("Error: bad request", 400);
        }

        LoginResult result = userService.login(body.username(), body.password());

        return new Gson().toJson(result);

    }

    private Object Logout(Request req, Response res) throws DataAccessException {
        String token = req.headers("Authorization");

        userService.logout(token);

        return new Gson().toJson(null);
    }

    private Object Clear(Request req, Response res){
        appService.clear();

        return new Gson().toJson(null);
    }

    private Object ListGames(Request req, Response res) throws DataAccessException {
        String token = req.headers("Authorization");

        ListResult result = gameService.list(token);

        return new Gson().toJson(result);
    }

    private Object CreateGame(Request req, Response res) throws DataAccessException {
        String token = req.headers("Authorization");
        var body = new Gson().fromJson(req.body(), CreateRequest.class);


        CreateResult result = gameService.createGame(token, body.gameName());

        return new Gson().toJson(result);
    }

    private Object JoinGame(Request req, Response res) throws DataAccessException {
        String token = req.headers("Authorization");

        JoinRequest body;
        try {
            body = new Gson().fromJson(req.body(), JoinRequest.class);
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new DataAccessException("Error: bad request", 400);
        }



        gameService.joinGame(token, body.gameID(), body.playerColor());

        return new Gson().toJson(null);
    }

}
