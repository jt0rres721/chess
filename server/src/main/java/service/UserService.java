package service;

import server.RegisterResult;
import server.RegisterRequest;

public class UserService {
    public static RegisterResult register(RegisterRequest req){


        return new RegisterResult("testuser", "testtoken");
    }


}
