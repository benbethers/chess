package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // Check if username is already used
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }

        // Add user to users hashmap
        users.put(user.username(), user);
    }

    // Retrieve a user from the users by username
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    // Clear all users
    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
}
