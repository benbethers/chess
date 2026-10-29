package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import io.javalin.Javalin;
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

        // Create data access objects
        var userDAO = new MySQLUserDAO();
        var gameDAO = new MySQLGameDAO();
        var authDAO = new MySQLAuthDAO();

        // Initialize services
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        // Clear all data (DELETE /db)
        javalin.delete("/db", ctx -> {
            try {
                clearService.clear();
                sendJson(ctx, 200, Map.of());
            } catch (DataAccessException e) {
                sendError(ctx, 500, e.getMessage());
            }
        });

        // Register a new user (POST /user)
        javalin.post("/user", ctx -> {
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

                sendJson(ctx, 200, Map.of(
                        "username", auth.username(),
                        "authToken", auth.authToken()
                ));

            } catch (JsonSyntaxException e) {
                sendError(ctx, 400, "Invalid JSON");
            } catch (DataAccessException e) {
                String message = e.getMessage();

                if (message != null &&
                        message.toLowerCase().contains("already taken")) {
                    sendError(ctx, 403, message);
                } else if (message != null &&
                        message.toLowerCase().contains("missing")) {
                    sendError(ctx, 400, message);
                } else {
                    sendError(ctx, 500, message);
                }
            }
        });

        // Log in (POST /session)
        javalin.post("/session", ctx -> {
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

                sendJson(ctx, 200, Map.of(
                        "username", auth.username(),
                        "authToken", auth.authToken()
                ));

            } catch (JsonSyntaxException e) {
                sendError(ctx, 400, "Invalid JSON");
            } catch (DataAccessException e) {
                String message = e.getMessage();

                if (message != null &&
                        message.toLowerCase().contains("missing")) {
                    sendError(ctx, 400, message);
                } else if (message != null &&
                        message.toLowerCase().contains("incorrect")) {
                    sendError(ctx, 401, message);
                } else {
                    sendError(ctx, 500, message);
                }
            }
        });

        // Log out (DELETE /session)
        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                userService.logout(authToken);
                sendJson(ctx, 200, Map.of());

            } catch (DataAccessException e) {
                String message = e.getMessage();

                if (message != null &&
                        (message.toLowerCase().contains("token") ||
                                message.toLowerCase().contains("authorization"))) {
                    sendError(ctx, 401, message);
                } else {
                    sendError(ctx, 500, message);
                }
            }
        });

        // List games (GET /game)
        javalin.get("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                Collection<GameData> games =
                        gameService.listGames(authToken);

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
                String message = e.getMessage();

                if (message != null &&
                        (message.toLowerCase().contains("token") ||
                                message.toLowerCase().contains("authorization"))) {
                    sendError(ctx, 401, message);
                } else {
                    sendError(ctx, 500, message);
                }
            }
        });

        // Create a game (POST /game)
        javalin.post("/game", ctx -> {
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
                String message = e.getMessage();

                if (message != null &&
                        message.toLowerCase().contains("game name")) {
                    sendError(ctx, 400, message);
                } else if (message != null &&
                        (message.toLowerCase().contains("token") ||
                                message.toLowerCase().contains("authorization"))) {
                    sendError(ctx, 401, message);
                } else {
                    sendError(ctx, 500, message);
                }
            }
        });

        // Join a game (PUT /game)
        javalin.put("/game", ctx -> {
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
                String message = e.getMessage();
                String lowerMessage = message == null
                        ? ""
                        : message.toLowerCase();

                if (lowerMessage.contains("token") ||
                        lowerMessage.contains("authorization")) {
                    sendError(ctx, 401, message);
                } else if (lowerMessage.contains("occupied") ||
                        lowerMessage.contains("already taken")) {
                    sendError(ctx, 403, message);
                } else if (lowerMessage.contains("game") ||
                        lowerMessage.contains("color") ||
                        lowerMessage.contains("player")) {
                    sendError(ctx, 400, message);
                } else {
                    sendError(ctx, 500, message);
                }
            }
        });
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Could not initialize database", ex);
        }

        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void sendJson(
            io.javalin.http.Context ctx,
            int status,
            Object response
    ) {
        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(response));
    }

    private void sendError(
            io.javalin.http.Context ctx,
            int status,
            String message
    ) {
        sendJson(ctx, status, Map.of(
                "message",
                "Error: " + (message == null ? "Unknown error" : message)
        ));
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