package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {

    private MySQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();

        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    void createAndGetUser() throws DataAccessException {
        UserData user = new UserData(
                "user1",
                "hashedPassword123",
                "user1@example.com"
        );

        userDAO.createUser(user);

        assertEquals(user, userDAO.getUser("user1"));
    }

    @Test
    void getNonexistentUser() throws DataAccessException {
        assertNull(userDAO.getUser("nonexistent"));
    }

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