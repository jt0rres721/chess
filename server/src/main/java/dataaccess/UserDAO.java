package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser (String username);

    void addUser(String username, String password, String email);

    void clear();

    int size();
}



