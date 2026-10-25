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
        String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new AuthData(
                            result.getString("authToken"),
                            result.getString("username")
                    );
                }
            }

            return null;

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to get authentication token", ex);
        }
    }

    // Remove authentication token from the authTokens hashmap
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new DataAccessException("Authentication token does not exist");
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to delete authentication token", ex);
        }
    }


    // Clear all auth tokens
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to clear authentication tokens", ex);
        }
    }
}
