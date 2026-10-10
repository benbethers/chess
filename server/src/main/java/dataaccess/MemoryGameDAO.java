package dataaccess;

import model.GameData;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = nextGameID++;

        GameData game = new GameData(
                gameID,
                null,
                null,
                gameName,
                new ChessGame()
        );

        games.put(gameID, game);
        return gameID;
    }
}
