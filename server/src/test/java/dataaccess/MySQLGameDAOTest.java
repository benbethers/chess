package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTest {

    private MySQLGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();

        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    @Test
    void createAndGetGame() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");

        GameData game = gameDAO.getGame(gameID);

        assertNotNull(game);
        assertEquals(gameID, game.gameID());
        assertEquals("game1", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals(new ChessGame(), game.game());
    }

    @Test
    void getNonexistentGame() throws DataAccessException {
        assertNull(gameDAO.getGame(-1));
    }

    @Test
    void createMultipleGames() throws DataAccessException {
        int game1 = gameDAO.createGame("game1");
        int game2 = gameDAO.createGame("game2");

        assertNotEquals(game1, game2);
    }

    @Test
    void listGames() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        Collection<GameData> games = gameDAO.listGames();

        assertEquals(2, games.size());
    }

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

    @Test
    void clearGames() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }
}