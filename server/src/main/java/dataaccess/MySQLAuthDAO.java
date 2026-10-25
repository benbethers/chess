package dataaccess;

import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        authTokens.put(auth.authToken(), auth);
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
