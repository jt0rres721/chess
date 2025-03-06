package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new MemoryUserDAO());
    }

    // Positive Test
    @Test
    void testRegisterSuccess() throws DataAccessException {
        userService.register("Alice", "securePass", "alice@example.com");


        UserData storedUser = userService.getUser("Alice", "securePass");
        assertNotNull(storedUser);
        assertEquals("Alice", storedUser.username());
        assertEquals("securePass", storedUser.password());
        assertEquals("alice@example.com", storedUser.email());
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
