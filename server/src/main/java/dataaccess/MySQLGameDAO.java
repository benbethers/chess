package dataaccess;

import model.GameData;
import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String sql = """
                INSERT INTO games (whiteUsername, blackUsername, gameName, game)
                VALUES (?, ?, ?, ?)
                """;

        ChessGame chessGame = new ChessGame();
        String gameJson = gson.toJson(chessGame);

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setNull(1, java.sql.Types.VARCHAR);
            statement.setNull(2, java.sql.Types.VARCHAR);
            statement.setString(3, gameName);
            statement.setString(4, gameJson);

            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new DataAccessException("Unable to create game");

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create game", ex);
        }
    }

    // Retrieve game from MySQL database
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setInt(1, gameID);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new GameData(
                            result.getInt("gameID"),
                            result.getString("whiteUsername"),
                            result.getString("blackUsername"),
                            result.getString("gameName"),
                            gson.fromJson(result.getString("game"), ChessGame.class)
                    );
                }
            }

            return null;

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to get game", ex);
        }
    }

    // Return a list of all games
    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql);
             var result = statement.executeQuery()) {

            while (result.next()) {
                games.add(new GameData(
                        result.getInt("gameID"),
                        result.getString("whiteUsername"),
                        result.getString("blackUsername"),
                        result.getString("gameName"),
                        gson.fromJson(result.getString("game"), ChessGame.class)
                ));
            }

            return games;

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to list games", ex);
        }
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

    // Clear game list and reset gameIDs counter
    @Override
    public void clear() throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }
}