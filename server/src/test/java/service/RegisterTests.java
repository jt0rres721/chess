package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.RegisterResult;
import server.LoginResult;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    private UserService userService;
    private UserDAO userData;
    private AuthDAO authData;

    @BeforeEach
    void setUp() {
        userData = new MemoryUserDAO();
        authData = new MemoryAuthDAO();
        userService = new UserService(userData, authData);
    }

    // Positive Test
    @Test
    void testRegisterSuccess() throws DataAccessException {
        RegisterResult result = userService.register("Alice", "securePass", "alice@example.com");

        assertNotNull(result);
        assertEquals("Alice", result.username());


        UserData storedUser = userService.getUser("Alice");
        assertNotNull(storedUser);
        assertEquals("Alice", storedUser.username());
        assertEquals("securePass", storedUser.password());
        assertEquals("alice@example.com", storedUser.email());

        AuthData storedAuth = userService.getToken(result.authToken());
        assertEquals("Alice", storedAuth.username());


    }

    // Negative Test
    @Test
    void testRegisterDuplicateUsername() throws DataAccessException {
        userService.register("Bob", "password123", "bob@example.com");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register("Bob", "newPassword", "newbob@example.com");
        });

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.StatusCode());
    }


    //positive login test
    @Test
    void testLogin() throws DataAccessException {
        this.userData.addUser("Jim", "pass", "email");

        LoginResult result = userService.login("Jim", "pass");
        assertNotNull(result);
        assertEquals("Jim", result.username());
        assertNotNull(result.authToken());


    }

    // negative login test
    @Test
    void testLoginWrongPasswordAndUser() throws DataAccessException{
        this.userData.addUser("Jim", "pass", "email");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("Bob", "newPassword");
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());

        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> {
            userService.login("Jim", "newPassword");
        });

        assertEquals("Error: unauthorized", exception2.getMessage());
        assertEquals(401, exception2.StatusCode());



    }

    //positive logout test
    @Test
    void testLogout() throws DataAccessException {
        this.userData.addUser("Jim", "pass", "email");

        LoginResult result = userService.login("Jim", "pass");
        assertNotNull(result);
        assertEquals("Jim", result.username());
        assertNotNull(result.authToken());

        String token = result.authToken();

        userService.logout(token);
        assertNull(this.authData.getToken(token));

    }


    //negative logout test
    @Test
    void testLogoutNegative() throws DataAccessException{
        this.userData.addUser("Jim", "pass", "email");

        LoginResult result = userService.login("Jim", "pass");
        assertNotNull(result);
        assertEquals("Jim", result.username());
        assertNotNull(result.authToken());

        String token = result.authToken();

        //logout wrong token
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.logout("bilbobaggins");
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());

        //logout twice
        userService.logout(token);
        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> {
            userService.logout(token);
        });

        assertEquals("Error: unauthorized", exception2.getMessage());
        assertEquals(401, exception2.StatusCode());



    }




}
