package ca.udem.maville;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;


import java.util.*;

public class Server {

    public static Javalin app;
    static String ErrorMessage = "Il y a eu une erreur (default)";

    static List<String> messageQueue = new ArrayList<>();

    static List<User> userList = new ArrayList<>();

    static User currentUser;

    private static int port = 7000;

    public static void main(String[] args) {
        app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(port);

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(String.format("http://localhost:%d", port)));
        } catch (Exception e) {
            e.printStackTrace();
        }



        app.get("/api/hello_world", context -> {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hello from the backend!");
            context.json(response);
        });

        app.get("/events", ctx -> {
            // Set necessary headers for SSE
            ctx.header("Content-Type", "text/event-stream");
            ctx.header("Cache-Control", "no-cache");
            ctx.res().setCharacterEncoding("UTF-8");

            int numMessagesSent = 0;

            // Send events, for demonstration send a new one every 5 seconds
            while (!Thread.interrupted()) {
                while (numMessagesSent < messageQueue.size()) {
                    ctx.res().getWriter().write("data: " + messageQueue.get(numMessagesSent++) + "\n\n");
                    ctx.res().getWriter().flush();
                }
                Thread.sleep(1000); // chaque seconde on check pour un nouveaux message chaque seconde
            }
        });

        app.post("/api/resident-log-in", ctx -> {
            currentUser = ctx.bodyAsClass(User.class);
            ctx.json(currentUser);
            System.out.println(currentUser.getUsername());
        });

        app.post("/api/resident-form-send", ctx -> {

        });



    }

    public static void sendMessage(String msg){
        // Update our error message variable (if you want to use it elsewhere)
        messageQueue.add(msg);
    }

    public void showList(){

    }

    private void sendForm(){

    }

}