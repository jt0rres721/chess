package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData findUser (String username);

    void addUser(String username, String password, String email);
    }

