package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerSuccessfully() throws DataAccessException {
        AuthData auth = userService.register(
                "alice", "password123", "alice@example.com");

        assertNotNull(auth);
        assertEquals("alice", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(userDAO.getUser("alice"));
        assertNotNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void registerDuplicateUsername() throws DataAccessException {
        userService.register("alice", "password123", "alice@example.com");

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> userService.register(
                        "alice", "anotherPassword", "other@example.com"));

        assertTrue(exception.getMessage().contains("taken"));
    }

    @Test
    void registerMissingInformation() {
        assertThrows(
                DataAccessException.class,
                () -> userService.register("", "password123", "alice@example.com"));
    }

    @Test
    void loginSuccessfully() throws DataAccessException {
        userService.register("alice", "password123", "alice@example.com");

        AuthData auth = userService.login("alice", "password123");

        assertEquals("alice", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void loginWithIncorrectPassword() throws DataAccessException {
        userService.register("alice", "password123", "alice@example.com");

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> userService.login("alice", "wrongPassword"));

        assertTrue(exception.getMessage().contains("Incorrect"));
    }

    @Test
    void loginWithUnknownUsername() {
        assertThrows(
                DataAccessException.class,
                () -> userService.login("unknown", "password123"));
    }

    @Test
    void logoutSuccessfully() throws DataAccessException {
        AuthData auth = userService.register(
                "alice", "password123", "alice@example.com");

        userService.logout(auth.authToken());

        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void logoutWithInvalidToken() {
        assertThrows(
                DataAccessException.class,
                () -> userService.logout("invalid-token"));
    }
}
