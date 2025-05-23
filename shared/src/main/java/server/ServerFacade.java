package server;

import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url){
        this.serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request) throws SharedException {
        var path = "/user";

        return makeRequest("POST", path, request, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest request) throws SharedException{
        var path = "/session";
        return makeRequest("POST", path, request, LoginResult.class, null);
    }

    public void logout(String token) throws SharedException{
        var path = "/session";
        makeRequest("DELETE", path, null, null, token);
    }

    public ListResult listGames(String token) throws SharedException{
        var path = "/game";

        return makeRequest("GET", path, null, ListResult.class, token);
    }


    public CreateResult createGame(CreateRequest create, String token) throws SharedException{
        var path = "/game";

        return makeRequest("POST", path, create, CreateResult.class, token);
    }

    public void joinGame(JoinRequest request, String token)throws SharedException{
        var path = "/game";

        makeRequest("PUT", path, request, null, token);
    }

    public void clear() throws SharedException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String token) throws SharedException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (token != null){
                http.setRequestProperty("Authorization", token);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new SharedException(ex.getMessage(), 500);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, SharedException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw SharedException.fromJson(respErr);
                }
            }

            throw new SharedException("other failure: " + status, status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
