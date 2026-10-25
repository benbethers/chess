package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());

            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create authentication token", ex);
        }
    }

    // Return authToken from authTokens list;
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    // Remove authentication token from the authTokens hashmap
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Authentication token does not exist");
        }

        authTokens.remove(authToken);
    }

    // Clear all auth tokens
    @Override
    public void clear() throws DataAccessException {
        authTokens.clear();
    }
}
