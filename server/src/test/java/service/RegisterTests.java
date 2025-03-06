package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
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
            userService.register("Bob", "newPassword", "newbob@example.com"); // Attempt duplicate registration
        });

        assertEquals("Username already exists", exception.getMessage());
    }


}
