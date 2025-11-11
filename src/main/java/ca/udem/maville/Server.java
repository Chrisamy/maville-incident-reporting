package ca.udem.maville;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class Server {

    static Javalin app;
    static String ErrorMessage = "Il y a eu une erreur (default)";

    static Set<Context> sseClients = new HashSet<>();

    public static void main(String[] args) {
        app = Javalin.create(config -> {
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

        app.get("/events", ctx -> {
            // Configure HTTP headers to indicate this is an SSE stream
            ctx.res().setContentType("text/event-stream");
            ctx.res().setCharacterEncoding("UTF-8");
            ctx.res().setHeader("Cache-Control", "no-cache");

            // Add this connection to the list of clients
            sseClients.add(ctx);

            // When the connection is closed, remove it from the list
            ctx.future(() -> { sseClients.remove(ctx); return null;});

            // Flush the response to start the event stream
            ctx.res().flushBuffer();
        });

        // route that captures the last segment as the "msg" path parameter

        new Thread(() -> {
            try {
                // wait a few seconds to allow the browser to connect
                Thread.sleep(5000);
                sendMessage("Message");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();




    }

    public static void sendMessage(String msg){
        // Update our error message variable (if you want to use it elsewhere)
        ErrorMessage = msg;


        // For every connected client...
        sseClients.forEach(ctx -> {
            try {
                // SSE messages are sent in the format "data: message\n\n"
                ctx.res().getWriter().write("data: " + msg + "\n\n");

                // Force the data to actually be sent immediately
                ctx.res().flushBuffer();
            } catch (IOException e) {
                // If a client disconnects or errors out, remove it
                sseClients.remove(ctx);
            }
        });
    }

}