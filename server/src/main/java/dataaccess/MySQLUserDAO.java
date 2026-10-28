package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {
    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = """
                INSERT INTO users (username, password, email)
                VALUES (?, ?, ?)
                """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.email());

            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create user", ex);
        }
    }

    // Retrieve a user from the users by username
    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, username);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new UserData(
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("email")
                    );
                }
            }

            return null;

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to get user", ex);
        }
    }

    // Clear all users
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to clear users", ex);
        }
    }
}
