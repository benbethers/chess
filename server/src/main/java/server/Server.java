package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.Javalin;
import model.AuthData;
import service.ClearService;
import service.GameService;
import service.UserService;

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
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var authDAO = new MemoryAuthDAO();

        // Initialize services
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        // Clear all data (/db)
        javalin.delete("/db", ctx -> {
            try {
                clearService.clear();
                sendJson(ctx, 200, Map.of());
            } catch (DataAccessException e) {
                sendError(ctx, 500, e.getMessage());
            }
        });

        // Register a new user (/user)
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
    }

    public int run(int desiredPort) {
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
}