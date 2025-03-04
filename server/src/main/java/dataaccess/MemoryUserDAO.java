package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();


    @Override
    public UserData findUser(String username) {
        return users.get(username);
    }

    @Override
    public void addUser(String username, String password, String email){

    }
}
