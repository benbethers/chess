package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {

    private MySQLUserDAO userDAO;

    // Set up tester database before every test
    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();

        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    // Test making a user
    @Test
    void createUser() throws DataAccessException {
        UserData user = new UserData(
                "user1",
                "hashedPassword123",
                "user1@example.com"
        );

        assertDoesNotThrow(() -> userDAO.createUser(user));
    }

    // Test getting a user
    @Test
    void getUser() throws DataAccessException {
        UserData user = new UserData(
                "user1",
                "hashedPassword123",
                "user1@example.com"
        );

        userDAO.createUser(user);

        assertEquals(user, userDAO.getUser("user1"));
    }

    // Test trying to retrieve a user that does not exist
    @Test
    void getNonexistentUser() throws DataAccessException {
        assertNull(userDAO.getUser("doesnotexist"));
    }

    // Test when someone attempts to make a duplicate user
    @Test
    void createDuplicateUser() throws DataAccessException {
        UserData user = new UserData(
                "user1",
                "hashedPassword123",
                "user1@example.com"
        );

        userDAO.createUser(user);

        assertThrows(
                DataAccessException.class,
                () -> userDAO.createUser(user)
        );
    }

    // Clear users test
    @Test
    void clearUsers() throws DataAccessException {
        userDAO.createUser(new UserData(
                "user1", "password1", "user1@example.com"
        ));

        userDAO.createUser(new UserData(
                "user2", "password2", "user2@example.com"
        ));

        userDAO.clear();

        assertNull(userDAO.getUser("user1"));
        assertNull(userDAO.getUser("user2"));
    }
}