package server;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


public class ServerException extends Exception {
  private final int code;
  public ServerException(String message, int code) {
      super(message);
      this.code = code;
  }

//  public String toJson() {
//    return new Gson().toJson(Map.of("message", getMessage(), "status", code));
//  }

  public static ServerException fromJson(InputStream stream) {
    var mapp = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
    var status = ((Double)mapp.get("status")).intValue();
    String message = mapp.get("message").toString();
    return new ServerException(message, status);
  }


  public int getCode() {
    return code;
  }
}

