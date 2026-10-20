
package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private GameService gameService;
    private String aliceToken;
    private String bobToken;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);

        aliceToken = "alice-token";
        bobToken = "bob-token";

        authDAO.createAuth(new AuthData(aliceToken, "alice"));
        authDAO.createAuth(new AuthData(bobToken, "bob"));
    }

    @Test
    void createGameSuccessfully() throws DataAccessException {
        int gameID = gameService.createGame(aliceToken, "Chess Match");

        assertTrue(gameID > 0);

        GameData game = gameDAO.getGame(gameID);
        assertNotNull(game);
        assertEquals("Chess Match", game.gameName());
    }

    @Test
    void createGameWithMissingName() {
        assertThrows(
                DataAccessException.class,
                () -> gameService.createGame(aliceToken, ""));
    }

    @Test
    void createGameWithInvalidToken() {
        assertThrows(
                DataAccessException.class,
                () -> gameService.createGame("invalid-token", "Chess Match"));
    }

    @Test
    void listGamesSuccessfully() throws DataAccessException {
        gameService.createGame(aliceToken, "First Game");
        gameService.createGame(aliceToken, "Second Game");

        Collection<GameData> games = gameService.listGames(aliceToken);

        assertEquals(2, games.size());
    }

    @Test
    void listGamesWithInvalidToken() {
        assertThrows(
                DataAccessException.class,
                () -> gameService.listGames("invalid-token"));
    }

    @Test
    void joinGameAsWhite() throws DataAccessException {
        int gameID = gameService.createGame(aliceToken, "Chess Match");

        gameService.joinGame(aliceToken, gameID, "WHITE");

        GameData game = gameDAO.getGame(gameID);
        assertEquals("alice", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameAsBlack() throws DataAccessException {
        int gameID = gameService.createGame(aliceToken, "Chess Match");

        gameService.joinGame(bobToken, gameID, "BLACK");

        GameData game = gameDAO.getGame(gameID);
        assertEquals("bob", game.blackUsername());
        assertNull(game.whiteUsername());
    }

    @Test
    void joinGameWhenWhiteSlotIsOccupied() throws DataAccessException {
        int gameID = gameService.createGame(aliceToken, "Chess Match");
        gameService.joinGame(aliceToken, gameID, "WHITE");

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame(bobToken, gameID, "WHITE"));

        assertTrue(exception.getMessage().contains("occupied"));
    }

    @Test
    void joinGameWithUnknownGameID() {
        assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame(aliceToken, 999, "WHITE"));
    }

    @Test
    void joinGameWithInvalidColor() throws DataAccessException {
        int gameID = gameService.createGame(aliceToken, "Chess Match");

        assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame(aliceToken, gameID, "GREEN"));
    }

    @Test
    void joinGameWithInvalidToken() throws DataAccessException {
        int gameID = gameService.createGame(aliceToken, "Chess Match");

        assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame("invalid-token", gameID, "WHITE"));
    }
}
