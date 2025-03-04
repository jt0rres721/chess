package service;

import model.UserData;
import server.RegisterResult;
import server.RegisterRequest;

public class UserService {
    public static RegisterResult register(RegisterRequest req){


        return new RegisterResult("testuser", "testtoken");
    }

    public UserData getUser(String username, String password){
        return
    }


}
