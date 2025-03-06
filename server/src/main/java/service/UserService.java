package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import server.RegisterResult;
//import server.RegisterRequest;
import dataaccess.UserDAO;
import java.util.UUID;


public class UserService {

    private final UserDAO userDBase;
    private final AuthDAO authDBase;

    public UserService(UserDAO userData, AuthDAO authDBase) {
        this.userDBase = userData;
        this.authDBase = authDBase;
    }

    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        if (getUser(username) == null ){
            this.userDBase.addUser(username, password, email);
        } else {throw new DataAccessException("Username already exists");}

        String token = generateToken();
        this.authDBase.addToken(token, username);


        return new RegisterResult(username, token);
    }

    public UserData getUser(String username){
        return userDBase.getUser(username);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData getToken(String token){
        return authDBase.getToken(token);
    }


}
