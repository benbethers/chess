package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // Register a new user to the authTokens list and create a new user
    public AuthData register(String username, String password, String email) throws DataAccessException {
        // Check that all fields are provided
        if (username == null || username.isBlank()
            || password == null || password.isBlank()
            || email == null || email.isBlank()) {
            throw new DataAccessException("Missing information");
        }

        // Check if username is already being used
        if (userDAO.getUser(username) != null) {
            throw new DataAccessException("Username is already taken");
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Create and add new user
        UserData user = new UserData(username, hashedPassword, email);
        userDAO.createUser(user);

        // Generate and store an authentication token
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        authDAO.createAuth(auth);

        return auth;
    }

    // Login user
    public AuthData login(String username, String password) throws DataAccessException {
        // Check that all fields are provided
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new DataAccessException("Missing information");
        }

        UserData user = userDAO.getUser(username);

        // Verify that user exists and that the password is correct
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("Incorrect username or password");
        }

        // Make and store authentication token
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        authDAO.createAuth(auth);

        return auth;
    }

    //Logout user
    public void logout(String authToken) throws DataAccessException {
        // Make sure the token is provided
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Missing authentication token");
        }

        // Verify the token exists
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Invalid authentication token");
        }

        // Delete authentication token
        authDAO.deleteAuth(authToken);
    }
}
