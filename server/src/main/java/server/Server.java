
package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        javalin = Javalin.create(
                config -> config.staticFiles.add("web")
        );

        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var authDAO = new MemoryAuthDAO();

        clearService = new ClearService(userDAO, gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        registerRoutes();
    }

    private void registerRoutes() {
        javalin.delete("/db", this::clearDatabase);
        javalin.post("/user", this::registerUser);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    private void clearDatabase(Context ctx) {
        try {
            clearService.clear();
            sendJson(ctx, 200, Map.of());
        } catch (DataAccessException e) {
            sendError(ctx, 500, e.getMessage());
        }
    }

    private void registerUser(Context ctx) {
        try {
            RegisterRequest request =
                    gson.fromJson(ctx.body(), RegisterRequest.class);

            if (request == null) {
                sendError(ctx, 400, "Missing request body");
                return;
            }

            AuthData auth = userService.register(
                    request.username(),
                    request.password(),
                    request.email()
            );

            sendAuthResponse(ctx, auth);
        } catch (JsonSyntaxException e) {
            sendError(ctx, 400, "Invalid JSON");
        } catch (DataAccessException e) {
            sendRegistrationError(ctx, e.getMessage());
        }
    }

    private void login(Context ctx) {
        try {
            LoginRequest request =
                    gson.fromJson(ctx.body(), LoginRequest.class);

            if (request == null) {
                sendError(ctx, 400, "Missing request body");
                return;
            }

            AuthData auth = userService.login(
                    request.username(),
                    request.password()
            );

            sendAuthResponse(ctx, auth);
        } catch (JsonSyntaxException e) {
            sendError(ctx, 400, "Invalid JSON");
        } catch (DataAccessException e) {
            sendLoginError(ctx, e.getMessage());
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            userService.logout(authToken);
            sendJson(ctx, 200, Map.of());
        } catch (DataAccessException e) {
            sendAuthorizationOrServerError(ctx, e.getMessage());
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            Collection<GameData> games = gameService.listGames(authToken);

            var gameList = games.stream()
                    .map(game -> new GameListItem(
                            game.gameID(),
                            game.whiteUsername(),
                            game.blackUsername(),
                            game.gameName()
                    ))
                    .toList();

            sendJson(ctx, 200, Map.of("games", gameList));
        } catch (DataAccessException e) {
            sendAuthorizationOrServerError(ctx, e.getMessage());
        }
    }

    private void createGame(Context ctx) {
        try {
            CreateGameRequest request =
                    gson.fromJson(ctx.body(), CreateGameRequest.class);

            if (request == null) {
                sendError(ctx, 400, "Missing request body");
                return;
            }

            String authToken = ctx.header("Authorization");
            int gameID = gameService.createGame(
                    authToken,
                    request.gameName()
            );

            sendJson(ctx, 200, Map.of("gameID", gameID));
        } catch (JsonSyntaxException e) {
            sendError(ctx, 400, "Invalid JSON");
        } catch (DataAccessException e) {
            sendCreateGameError(ctx, e.getMessage());
        }
    }

    private void joinGame(Context ctx) {
        try {
            JoinGameRequest request =
                    gson.fromJson(ctx.body(), JoinGameRequest.class);

            if (request == null) {
                sendError(ctx, 400, "Missing request body");
                return;
            }

            String authToken = ctx.header("Authorization");
            gameService.joinGame(
                    authToken,
                    request.gameID(),
                    request.playerColor()
            );

            sendJson(ctx, 200, Map.of());
        } catch (JsonSyntaxException e) {
            sendError(ctx, 400, "Invalid JSON");
        } catch (DataAccessException e) {
            sendJoinGameError(ctx, e.getMessage());
        }
    }

    private void sendAuthResponse(Context ctx, AuthData auth) {
        sendJson(ctx, 200, Map.of(
                "username", auth.username(),
                "authToken", auth.authToken()
        ));
    }

    private void sendRegistrationError(Context ctx, String message) {
        if (containsIgnoreCase(message, "already taken")) {
            sendError(ctx, 403, message);
        } else if (containsIgnoreCase(message, "missing")) {
            sendError(ctx, 400, message);
        } else {
            sendError(ctx, 500, message);
        }
    }

    private void sendLoginError(Context ctx, String message) {
        if (containsIgnoreCase(message, "missing")) {
            sendError(ctx, 400, message);
        } else if (containsIgnoreCase(message, "incorrect")) {
            sendError(ctx, 401, message);
        } else {
            sendError(ctx, 500, message);
        }
    }

    private void sendAuthorizationOrServerError(
            Context ctx,
            String message
    ) {
        if (isAuthorizationError(message)) {
            sendError(ctx, 401, message);
        } else {
            sendError(ctx, 500, message);
        }
    }

    private void sendCreateGameError(Context ctx, String message) {
        if (containsIgnoreCase(message, "game name")) {
            sendError(ctx, 400, message);
        } else if (isAuthorizationError(message)) {
            sendError(ctx, 401, message);
        } else {
            sendError(ctx, 500, message);
        }
    }

    private void sendJoinGameError(Context ctx, String message) {
        if (isAuthorizationError(message)) {
            sendError(ctx, 401, message);
        } else if (containsIgnoreCase(message, "occupied")
                || containsIgnoreCase(message, "already taken")) {
            sendError(ctx, 403, message);
        } else if (containsIgnoreCase(message, "game")
                || containsIgnoreCase(message, "color")
                || containsIgnoreCase(message, "player")) {
            sendError(ctx, 400, message);
        } else {
            sendError(ctx, 500, message);
        }
    }

    private boolean isAuthorizationError(String message) {
        return containsIgnoreCase(message, "token")
                || containsIgnoreCase(message, "authorization");
    }

    private boolean containsIgnoreCase(String message, String phrase) {
        return message != null
                && message.toLowerCase().contains(phrase);
    }

    private void sendJson(Context ctx, int status, Object response) {
        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(response));
    }

    private void sendError(Context ctx, int status, String message) {
        sendJson(ctx, status, Map.of(
                "message",
                "Error: " + (message == null ? "Unknown error" : message)
        ));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private record RegisterRequest(
            String username,
            String password,
            String email
    ) {}

    private record LoginRequest(
            String username,
            String password
    ) {}

    private record CreateGameRequest(
            String gameName
    ) {}

    private record JoinGameRequest(
            int gameID,
            String playerColor
    ) {}

    private record GameListItem(
            int gameID,
            String whiteUsername,
            String blackUsername,
            String gameName
    ) {}
}