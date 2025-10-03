package ca.udem.maville;

import io.javalin.Javalin;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(7000);

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://localhost:7000"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        app.get("/api/hello_world", context -> {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hello from the backend!");
            context.json(response);
        });
    }
}