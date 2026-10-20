
package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private ClearService clearService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();

        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    void clearAllData() throws DataAccessException {
        userDAO.createUser(
                new UserData("alice", "password123", "alice@example.com"));

        int gameID = gameDAO.createGame("Chess Match");

        authDAO.createAuth(new AuthData("alice-token", "alice"));

        assertNotNull(userDAO.getUser("alice"));
        assertNotNull(gameDAO.getGame(gameID));
        assertNotNull(authDAO.getAuth("alice-token"));

        clearService.clear();

        assertNull(userDAO.getUser("alice"));
        assertTrue(gameDAO.listGames().isEmpty());
        assertNull(authDAO.getAuth("alice-token"));
    }

    @Test
    void clearEmptyData() throws DataAccessException {
        assertDoesNotThrow(() -> clearService.clear());

        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    void clearMultipleTimes() throws DataAccessException {
        clearService.clear();
        assertDoesNotThrow(() -> clearService.clear());

        assertTrue(gameDAO.listGames().isEmpty());
        assertNull(userDAO.getUser("alice"));
        assertNull(authDAO.getAuth("alice-token"));
    }
}
