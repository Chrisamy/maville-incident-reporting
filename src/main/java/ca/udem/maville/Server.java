package ca.udem.maville;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import java.util.*;

public class Server {

    public static Javalin app;

    private static List<String> messageQueue = new ArrayList<>();

    public static ProblemRepository problemList = ProblemRepository.getInstance();
    public static DemandRepository demandeList = DemandRepository.getInstance();

    static Resident currentResident;

    public static void main(String[] args) {
        int port = 7000;
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
            currentResident = ctx.bodyAsClass(Resident.class);
            ctx.json(currentResident);
            System.out.println(currentResident.getUsername());
        });

        app.post("/api/resident-form-send", ctx -> {
            FormResident formResident = new FormResident(ctx.formParam("address"), currentResident.getUsername(),
                    ctx.formParam("details"));
            ctx.json(formResident);
            currentResident.submitForm(formResident);
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