package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTest {

    private MySQLGameDAO gameDAO;

    // Set up tester database before every test
    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();

        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    // Test making a game
    @Test
    void createGame() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");

        assertTrue(gameID > 0);
    }

    // Test attempting to create a game with no name
    @Test
    void createGameWithoutName() {
        assertThrows(
                DataAccessException.class,
                () -> gameDAO.createGame(null)
        );
    }

    // Test getting a game
    @Test
    void getGame() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");

        GameData game = gameDAO.getGame(gameID);

        assertNotNull(game);
        assertEquals(gameID, game.gameID());
        assertEquals("game1", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals(new ChessGame(), game.game());
    }

    // Test attempting to get a game that does not exist
    @Test
    void getNonexistentGame() throws DataAccessException {
        assertNull(gameDAO.getGame(-1));
    }

    // Test making multiple games
    @Test
    void createMultipleGames() throws DataAccessException {
        int game1 = gameDAO.createGame("game1");
        int game2 = gameDAO.createGame("game2");

        assertNotEquals(game1, game2);
    }

    // Test the list games method
    @Test
    void listGames() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        Collection<GameData> games = gameDAO.listGames();

        assertEquals(2, games.size());
    }

    // Test listing games when no games exist
    @Test
    void listGamesWhenEmpty() throws DataAccessException {
        assertTrue(gameDAO.listGames().isEmpty());
    }

    // Test updating a game
    @Test
    void updateGame() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");

        GameData updatedGame = new GameData(
                gameID,
                "whiteUser",
                "blackUser",
                "updatedGame",
                new ChessGame()
        );

        gameDAO.updateGame(updatedGame);

        GameData result = gameDAO.getGame(gameID);

        assertEquals(updatedGame, result);
    }

    // Test attempting to update a game that does not exist
    @Test
    void updateNonexistentGame() {
        GameData game = new GameData(
                -1,
                "whiteUser",
                "blackUser",
                "game1",
                new ChessGame()
        );

        assertThrows(
                DataAccessException.class,
                () -> gameDAO.updateGame(game)
        );
    }

    // Test clearing all games
    @Test
    void clearGames() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }
}