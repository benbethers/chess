package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {

    private MySQLAuthDAO authDAO;

    // Set up tester database before every test
    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();

        authDAO = new MySQLAuthDAO();
        authDAO.clear();
    }

    // Test making an auth token
    @Test
    void createAuth() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");

        assertDoesNotThrow(() -> authDAO.createAuth(auth));
    }

    // Test getting an auth token
    @Test
    void getAuth() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");

        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("token123"));
    }

    // Test attempting to retrieve an auth token that does not exist
    @Test
    void getNonexistentAuth() throws DataAccessException {
        assertNull(authDAO.getAuth("nonexistent"));
    }

    // Test deleting an auth token
    @Test
    void deleteAuth() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");

        authDAO.createAuth(auth);
        authDAO.deleteAuth("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    // Test attempting to delete an auth token that does not exist
    @Test
    void deleteNonexistentAuth() {
        assertThrows(
                DataAccessException.class,
                () -> authDAO.deleteAuth("nonexistent")
        );
    }

    // Test clearing the auth token database
    @Test
    void clearAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("token1", "user1"));
        authDAO.createAuth(new AuthData("token2", "user2"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }

    // Test creating a duplicate auth token
    @Test
    void createDuplicateAuth() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");

        authDAO.createAuth(auth);

        assertThrows(
                DataAccessException.class,
                () -> authDAO.createAuth(auth)
        );
    }
}