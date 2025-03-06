package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;
import server.RegisterResult;
//import server.RegisterRequest;
import dataaccess.UserDAO;

public class UserService {

    private final UserDAO userData;

    public UserService(UserDAO userData) {
        this.userData = userData;
    }

    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        if (this.userData.getUser(username) == null ){
            this.userData.addUser(username, password, email);
        } else {throw new DataAccessException("Username already exists");}

        //TODO continue here: implement tests and add the authtoken

        return new RegisterResult("testuser", "testtoken");
    }

    public UserData getUser(String username, String password){
        return userData.getUser(username);
    }


}
