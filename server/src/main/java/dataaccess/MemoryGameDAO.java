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

    // Retrieve game from games hashmap
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    // Update game with new game object
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // Check if game exists before trying to update it
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game does not exist");
        }

        // Replace old game with updated game object
        games.put(game.gameID(), game);
    }


}
