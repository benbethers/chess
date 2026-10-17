package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        // Make sure that the game name is provided
        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Missing game name");
        }

        // Verify that the user is logged in
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Invalid authentication token");
        }

        // Create the game and return the gameID
        return gameDAO.createGame(gameName);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        // Verify that the user is logged in
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Invalid authentication token");
        }

        // Return all games
        return gameDAO.listGames();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        // Verify that the user is logged in
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Invalid authentication token");
        }

        // Find game
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Game does not exist");
        }

        if (playerColor == null
            || (!playerColor.equalsIgnoreCase("WHITE")
            && !playerColor.equalsIgnoreCase("BLACK"))
        ) {
            throw new DataAccessException("Invalid player color");
        }

        // Add the user to the requested player spot
        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("White player spot is occupied");
            }

            gameDAO.updateGame(new GameData(
                    game.gameID(),
                    auth.username(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            ));
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Black player spot is occupied");
            }

            gameDAO.updateGame(new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    auth.username(),
                    game.gameName(),
                    game.game()
            ));
        }
    }
}
