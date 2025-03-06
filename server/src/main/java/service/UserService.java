package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import server.LoginResult;
import server.RegisterResult;
//import server.RegisterRequest;
import dataaccess.UserDAO;

//import javax.xml.crypto.Data;
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
        } else {
            throw new DataAccessException("Error: already taken", 403);
        }

        String token = createAuth(username);


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

    public String createAuth(String username) throws DataAccessException{
        String token = generateToken();

        if (authDBase.users().contains(username)){throw new DataAccessException("Error: unauthorized", 401);}
        this.authDBase.addToken(token, username);
        return token;
    }

    public LoginResult login(String username, String password) throws DataAccessException{
        if (getUser(username) != null){
            if(getUser(username).password().equals(password)){
                String token = createAuth(username);

                return new LoginResult(username, token);


            } else {throw new DataAccessException("Error: unauthorized", 401);}
        } else {throw new DataAccessException("Error: unauthorized", 401);}
    }

    public void logout(String token) throws DataAccessException{
        if (getToken(token) != null){
            this.authDBase.deleteToken(token);
        } else {
            throw new DataAccessException("Error: unauthorized", 401);
        }
    }


}
