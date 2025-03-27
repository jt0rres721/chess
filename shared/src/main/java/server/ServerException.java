package server;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ServerException extends Exception {
  private final int statusCode;

  public ServerException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public String toJson() {
    return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
  }

  public static ServerException fromJson(InputStream stream) {
    var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
    var status = ((Double)map.get("status")).intValue();
    String message = map.get("message").toString();
    return new ServerException(message, status);
  }

  public int statusCode(){
    return statusCode;
  }


}