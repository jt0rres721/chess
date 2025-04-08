package service;

import dataaccess.AuthDAO;
import dataaccess.ServerException;
import model.AuthData;
import model.UserData;
import model.LoginResult;
import model.RegisterResult;
//import model.RegisterRequest;
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

    public RegisterResult register(String username, String password, String email) throws ServerException {
        if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
            throw new ServerException("Error: bad request", 400);
        }
        if (getUser(username) == null ){
            this.userDBase.addUser(username, password, email);
        } else {
            throw new ServerException("Error: already taken", 403);
        }

        String token = createAuth(username);


        return new RegisterResult(username, token);
    }

    public UserData getUser(String username) throws ServerException {
        return userDBase.getUser(username);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData getToken(String token) throws ServerException {
        return authDBase.getToken(token);
    }

    public String createAuth(String username) throws ServerException {//throws DataAccessException{
        String token = generateToken();

        //if (authDBase.users().contains(username)){throw new DataAccessException("Error: unauthorized", 401);}
        this.authDBase.addToken(token, username);
        return token;
    }

    public LoginResult login(String username, String password) throws ServerException {
        if (getUser(username) != null){
            if(userDBase.verifyUser(username, password)){
                String token = createAuth(username);

                return new LoginResult(username, token);


            } else {throw new ServerException("Error: unauthorized", 401);}
        } else {throw new ServerException("Error: unauthorized", 401);}
    }

    public void logout(String token) throws ServerException {
        if (getToken(token) != null){
            this.authDBase.deleteToken(token);
        } else {
            throw new ServerException("Error: unauthorized", 401);
        }
    }

    public String verifyUser(String token) throws ServerException {
        return authDBase.getUser(token);
    }


}
