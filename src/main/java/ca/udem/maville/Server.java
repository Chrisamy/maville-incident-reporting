package ca.udem.maville;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import java.util.*;

public class Server {

    public static Javalin app;

    // in-memory notifications list
    public static java.util.List<Notification> notifications = new java.util.ArrayList<>();

    // JSON persistence for notifications
    private static final ObjectMapper NOTIF_MAPPER = new ObjectMapper();
    private static final String NOTIF_FILE = "src/main/resources/public/JSON_files/notifications.json";

    //protected static String MTLjson = "src/main/resources/public/JSON_files/donnees_mtl_stripped_down.json";

    private static final List<String> messageQueue = new ArrayList<>();

    public static ProblemRepository problemList = ProblemRepository.getInstance();
    public static AgentProblemFormHandler problemFormHandler = new AgentProblemFormHandler();

    public static DemandRepository demandeList = DemandRepository.getInstance();


    static Resident currentResident;

    public static void main(String[] args) throws IOException {
        /**=============================================================================================================
         PRELOAD SERVER WITH 3 RESIDENTS, 3 CONTRACTORS, 3 PROBLEMS and 3 DEMANDS
         =============================================================================================================*/


        /**=============================================================================================================
         LAUNCH OF THE SERVER
         =============================================================================================================*/
        int port = 7000;
        app = Javalin.create(config -> config.staticFiles.add("/public")).start(port);

        // load persisted notifications
        loadNotifications();

        Desktop desktop = Desktop.getDesktop();
        try {

            desktop.browse(new URI(String.format("http://localhost:%d", port)));
            // PLR TESTING : to open the page in resident directly
            //desktop.browse(new URI(String.format("http://localhost:7000/viewResident.html", port)));
            // PLR TESTING : to open the page in contractor directly
            //desktop.browse(new URI(String.format("http://localhost:7000/viewPrestataire.html", port)));
            // PLR TESTING : to open the page in agent directly
            //desktop.browse(new URI(String.format("http://localhost:%d/viewAgent.html", port)));

        } catch (Exception e) {
            e.printStackTrace();
        }



        app.get("/api/hello_world", context -> {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hello from the backend!");
            context.json(response);
        });

        // events doesn't let the server start manually

        /*app.get("/events", ctx -> {
            // Set necessary headers for SSE
            ctx.header("Content-Type", "text/event-stream");
            ctx.header("Cache-Control", "no-cache");
            ctx.res().setCharacterEncoding("UTF-8");

            int numMessagesSent = 0;

            // Send events, for demonstration send a new one every 5 seconds
            while (!Thread.interrupted()) {
                while (numMessagesSent < messageQueue.size()) {
                    ctx.res().getWriter().write("data: " + messageQueue.getFirst() + "\n\n");
                    ctx.res().getWriter().flush();
                    messageQueue.removeFirst();
                }
                Thread.sleep(1000); // chaque seconde on check pour un nouveaux message chaque seconde
            }
        }); *

        /**=============================================================================================================
         API MANIPULATION FOR THE RESIDENT
         =============================================================================================================*/

        app.post("/api/resident-log-in", ctx -> {
            currentResident = ctx.bodyAsClass(Resident.class);
            // persist username in session so notification endpoints can target this session
            if (currentResident != null && currentResident.getUsername() != null) {
                ctx.sessionAttribute("username", currentResident.getUsername());
                ctx.sessionAttribute("role", "resident");
            }
            ctx.json(currentResident);
            System.out.println("Utilisateur " + (currentResident==null?"null":currentResident.getUsername()) + " s'est connecté.");
        });

        app.post("/api/resident-form-send", ctx -> {
            // logs
            String addr = ctx.formParam("address");
            String details = ctx.formParam("details");
            System.out.println("[SERVER] /api/resident-form-send received: address='" + addr + "' details='" + details + "'");
            System.out.println("[SERVER] currentResident object: " + (currentResident == null ? "null" : currentResident.getUsername()));

            ProblemForm problemForm = new ProblemForm(addr, currentResident == null ? "anonymous" : currentResident.getUsername(), details);
            // read arrondissement from form param 'borough' if present and set enum
            String boroughParam = ctx.formParam("borough");
            if (boroughParam != null && !boroughParam.isEmpty()) {
                try {
                    problemForm.setBoroughId(EnumBoroughID.valueOf(boroughParam));
                } catch (Exception e) {
                    System.err.println("Invalid borough parameter: " + boroughParam);
                }
            }
            // set server-side submission timestamp
            problemForm.setDate(System.currentTimeMillis());

            System.out.println("[SERVER] created ProblemForm: " + problemForm);
            // add to repository (resident association if available)
            if (currentResident != null) {
                currentResident.submitForm(problemForm);
            } else {
                ProblemRepository.addForm(problemForm);
                System.out.println("[SERVER] No current resident; form saved in repository without association.");
            }

            // Persist problems list so the agent page (which can read JSON) sees the new submission
            try {
                ObjectMapper problemsMapper = new ObjectMapper();
                java.util.List<ProblemForm> list = problemList.getFormList();
                File problemsFileSrc = new File("src/main/resources/public/JSON_files/problems.json");
                File dirSrc = problemsFileSrc.getParentFile(); if (dirSrc!=null && !dirSrc.exists()) dirSrc.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileSrc, list);
                File problemsFileTarget = new File("target/public/JSON_files/problems.json");
                File dirTarget = problemsFileTarget.getParentFile(); if (dirTarget!=null && !dirTarget.exists()) dirTarget.mkdirs();
                try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTarget, list); } catch (Exception ignore) {}
                File problemsFileTargetClasses = new File("target/classes/public/JSON_files/problems.json");
                File dirTargetClasses = problemsFileTargetClasses.getParentFile(); if (dirTargetClasses!=null && !dirTargetClasses.exists()) dirTargetClasses.mkdirs();
                try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTargetClasses, list); } catch (Exception ignore) {}
            } catch (Exception e) { System.err.println("Failed to persist problems.json: " + e.getMessage()); }

            // Notify agent when resident submits a request (non-fatal)
            try {
                Notification notif = new Notification("agent", "Nouvelle demande", "Une nouvelle demande a été soumise par un résident");
                notifications.add(notif);
                saveNotifications();
            } catch (Exception e) { /* ignore notification errors */ }

            // return created form for client if needed
            ctx.json(problemForm);
        });

        /**=============================================================================================================
         API MANIPULATION FOR THE AGENT
         =============================================================================================================*/

        app.post("/api/agent-refuse-problem", ctx -> {

            String formId = ctx.formParam("id");
            // N.B.: the status change is implicit in the methode RefuseProblem
            problemFormHandler.RefuseProblem(problemList.getFormList(), formId);
            // Notify resident about refusal
            try {
                ProblemForm f = null;
                for (ProblemForm p : problemList.getFormList()) if (p.getId().equals(formId)) { f = p; break; }
                if (f != null && f.getUsername() != null) {
                    Notification notif = new Notification("resident", "Demande refusée", "Votre demande a été refusée par un agent.");
                    notif.setRole("resident");
                    // target this notification to the submitter so the resident sees it
                    notif.setUserId(f.getUsername());
                    notifications.add(notif);
                    saveNotifications();
                }

                // persist updated problems list so agent JSON source sees the change
                try {
                    ObjectMapper problemsMapper = new ObjectMapper();
                    java.util.List<ProblemForm> list = problemList.getFormList();
                    File problemsFileSrc = new File("src/main/resources/public/JSON_files/problems.json");
                    File dirSrc = problemsFileSrc.getParentFile(); if (dirSrc!=null && !dirSrc.exists()) dirSrc.mkdirs();
                    problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileSrc, list);
                    File problemsFileTarget = new File("target/public/JSON_files/problems.json");
                    File dirTarget = problemsFileTarget.getParentFile(); if (dirTarget!=null && !dirTarget.exists()) dirTarget.mkdirs();
                    try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTarget, list); } catch (Exception ignore) {}
                    File problemsFileTargetClasses = new File("target/classes/public/JSON_files/problems.json");
                    File dirTargetClasses = problemsFileTargetClasses.getParentFile(); if (dirTargetClasses!=null && !dirTargetClasses.exists()) dirTargetClasses.mkdirs();
                    try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTargetClasses, list); } catch (Exception ignore) {}
                } catch (Exception e) { System.err.println("Failed to persist problems.json after refuse: " + e.getMessage()); }

                Map<String,Object> out = new HashMap<>(); out.put("success", true); out.put("newStatus", (f==null?"rejected":f.getStatus().name())); ctx.json(out);
            } catch (Exception e) { /* ignore notification errors */ ctx.status(500); }
        });

        app.post("/api/agent-accept-problem", ctx -> {
            String formId = ctx.formParam("id");
            String workType = ctx.formParam("workType");
            String priority = ctx.formParam("priority");

            // Get the EnumPriority associated with the string (defensive)
            EnumWorkType newWorkType = EnumWorkType.notDefined;
            EnumPriority newPriority = EnumPriority.notAssigned;
            try { if (workType != null && !workType.isEmpty()) newWorkType = EnumWorkType.valueOf(workType); } catch (Exception ignored) {}
            try { if (priority != null && !priority.isEmpty()) newPriority = EnumPriority.valueOf(priority); } catch (Exception ignored) {}

            problemFormHandler.AcceptProblem(problemList.getFormList(), formId, newWorkType, newPriority);
            // Notify resident about approval and persist
            try {
                ProblemForm f = null;
                for (ProblemForm p : problemList.getFormList()) if (p.getId().equals(formId)) { f = p; break; }
                if (f != null && f.getUsername() != null) {
                    Notification notif = new Notification("resident", "Demande approuvée", "Votre demande a été approuvée par un agent.");
                    notif.setRole("resident");
                    notif.setUserId(f.getUsername());
                    notifications.add(notif);
                    saveNotifications();
                }

                // persist updated problems list so agent JSON source sees the change
                try {
                    ObjectMapper problemsMapper = new ObjectMapper();
                    java.util.List<ProblemForm> list = problemList.getFormList();
                    File problemsFileSrc = new File("src/main/resources/public/JSON_files/problems.json");
                    File dirSrc = problemsFileSrc.getParentFile(); if (dirSrc!=null && !dirSrc.exists()) dirSrc.mkdirs();
                    problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileSrc, list);
                    File problemsFileTarget = new File("target/public/JSON_files/problems.json");
                    File dirTarget = problemsFileTarget.getParentFile(); if (dirTarget!=null && !dirTarget.exists()) dirTarget.mkdirs();
                    try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTarget, list); } catch (Exception ignore) {}
                    File problemsFileTargetClasses = new File("target/classes/public/JSON_files/problems.json");
                    File dirTargetClasses = problemsFileTargetClasses.getParentFile(); if (dirTargetClasses!=null && !dirTargetClasses.exists()) dirTargetClasses.mkdirs();
                    try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTargetClasses, list); } catch (Exception ignore) {}
                } catch (Exception e) { System.err.println("Failed to persist problems.json after accept: " + e.getMessage()); }

                Map<String,Object> out = new HashMap<>(); out.put("success", true); out.put("newStatus", (f==null?"approved":f.getStatus().name())); ctx.json(out);
            } catch (Exception e) { /* ignore notification errors */ ctx.status(500); }
        });

        // Agent requests modification for a problem
        app.post("/api/agent-request-modification", ctx -> {
            String formId = ctx.formParam("id");
            String message = ctx.formParam("message");
            // set problem status to onHold via handler
            problemFormHandler.RefuseProblem(problemList.getFormList(), formId); // reuse refuse to set status? keep consistent
            for (ProblemForm p : problemList.getFormList()) {
                if (p.getId().equals(formId)) {
                    p.setStatus(EnumStatus.onHold);
                    break;
                }
            }
            // Notify resident about requested modification
            try {
                ProblemForm f = null;
                for (ProblemForm p : problemList.getFormList()) if (p.getId().equals(formId)) { f = p; break; }
                if (f != null && f.getUsername() != null) {
                    Notification notif = new Notification("resident", "Modification demandée", (message != null && !message.isEmpty()) ? ("Un agent a demandé : " + message) : "Un agent a demandé des modifications à votre demande.");
                    notif.setRole("resident");
                    notif.setUserId(f.getUsername());
                    notifications.add(notif);
                    saveNotifications();
                }

                // persist updated problems list so agent JSON source sees the change
                try {
                    ObjectMapper problemsMapper = new ObjectMapper();
                    java.util.List<ProblemForm> list = problemList.getFormList();
                    File problemsFileSrc = new File("src/main/resources/public/JSON_files/problems.json");
                    File dirSrc = problemsFileSrc.getParentFile(); if (dirSrc!=null && !dirSrc.exists()) dirSrc.mkdirs();
                    problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileSrc, list);
                    File problemsFileTarget = new File("target/public/JSON_files/problems.json");
                    File dirTarget = problemsFileTarget.getParentFile(); if (dirTarget!=null && !dirTarget.exists()) dirTarget.mkdirs();
                    try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTarget, list); } catch (Exception ignore) {}
                    File problemsFileTargetClasses = new File("target/classes/public/JSON_files/problems.json");
                    File dirTargetClasses = problemsFileTargetClasses.getParentFile(); if (dirTargetClasses!=null && !dirTargetClasses.exists()) dirTargetClasses.mkdirs();
                    try { problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTargetClasses, list); } catch (Exception ignore) {}
                } catch (Exception e) { System.err.println("Failed to persist problems.json after request-modification: " + e.getMessage()); }

                Map<String,Object> out = new HashMap<>(); out.put("success", true); out.put("newStatus", "onHold"); ctx.json(out);
            } catch (Exception e) { /* ignore notification errors */ ctx.status(500); }
        });

        app.post("/api/agent-problem-set-priority", ctx -> {
            // get user input
            String formId = ctx.formParam("problemid");
            String priority = ctx.formParam("newpriority");

            // Get the EnumPriority associated with the string
            EnumPriority newPriority = EnumPriority.valueOf(priority);

            problemFormHandler.AssignProblemPriority(problemList.getFormList(),formId, newPriority);
        });

        app.get("/api/load-problems", ctx -> {
            ctx.json(problemList.getFormList()); //loads list of problems to the front end (only for agent at this moment in time)
        });

        app.get("/api/load-demands", ctx -> {
            ctx.json(demandeList.getDemandList()); //loads list of problems to the front end (only for agent at this moment in time)
        });

        // Notifications endpoint (session or ?role= fallback)
        app.get("/notifications", ctx -> {
            String role = ctx.sessionAttribute("role");
            if (role == null) role = ctx.queryParam("role");
            String sessionUser = ctx.sessionAttribute("username");
            String qUser = ctx.queryParam("user");
            if (qUser != null && !qUser.isEmpty()) sessionUser = qUser;
            if (role == null) role = (sessionUser != null) ? "resident" : "agent";

            java.util.List<java.util.Map<String,Object>> out = new java.util.ArrayList<>();
            for (Notification n : notifications) {
                boolean targetedToCurrent = (n.getUserId() != null && sessionUser != null && n.getUserId().equals(sessionUser));
                if (role.equals(n.getRole()) || targetedToCurrent) {
                    java.util.Map<String,Object> m = new java.util.HashMap<>();
                    m.put("id", n.getId());
                    m.put("role", n.getRole());
                    m.put("title", n.getTitle());
                    m.put("message", n.getText());
                    m.put("timestamp", n.getTime());
                    m.put("read", n.isRead());
                    m.put("userId", n.getUserId());
                    out.add(m);
                }
            }
            ctx.json(out);
        });

        // compatibility route under /api
        app.get("/api/notifications", ctx -> {
            ctx.req().setAttribute("org.eclipse.jetty.server.Request.baseURI", ctx.req().getRequestURI());
            String role = ctx.sessionAttribute("role");
            if (role == null) role = ctx.queryParam("role");
            String sessionUser = ctx.sessionAttribute("username");
            String qUser = ctx.queryParam("user");
            if (qUser != null && !qUser.isEmpty()) sessionUser = qUser;
            if (role == null) role = (sessionUser != null) ? "resident" : "agent";

            java.util.List<java.util.Map<String,Object>> out = new java.util.ArrayList<>();
            for (Notification n : notifications) {
                boolean targetedToCurrent = (n.getUserId() != null && sessionUser != null && n.getUserId().equals(sessionUser));
                if (role.equals(n.getRole()) || targetedToCurrent) {
                    java.util.Map<String,Object> m = new java.util.HashMap<>();
                    m.put("id", n.getId());
                    m.put("role", n.getRole());
                    m.put("title", n.getTitle());
                    m.put("message", n.getText());
                    m.put("timestamp", n.getTime());
                    m.put("read", n.isRead());
                    m.put("userId", n.getUserId());
                    out.add(m);
                }
            }
            ctx.json(out);
        });

        // mark as read endpoints
        app.post("/notifications/mark-read/:id", ctx -> {
            String id = ctx.pathParam("id");
            for (Notification n : notifications) {
                if (n.getId().equals(id)) { n.setRead(true); break; }
            }
            // persist change
            saveNotifications();
            ctx.status(200);
        });

        // Minimal submission actions for agent UI
        app.post("/submissions/:id/approve", ctx -> {
            String id = ctx.pathParam("id");
            ProblemForm target = null;
            for (ProblemForm p : problemList.getFormList()) {
                if (p.getId().equals(id)) { p.setStatus(EnumStatus.approved); target = p; break; }
            }
            // notify resident (attach userId if found)
            try {
                Notification notif = new Notification("resident", "Demande approuvée", "Votre demande a été approuvée");
                if (target != null && target.getUsername() != null) notif.setUserId(target.getUsername());
                notifications.add(notif);
                saveNotifications();
            } catch (Exception ignored) {}

            // persist updated problems list so static copies also reflect the change
            try {
                ObjectMapper problemsMapper = new ObjectMapper();
                java.util.List<ProblemForm> list = problemList.getFormList();
                File problemsFileSrc = new File("src/main/resources/public/JSON_files/problems.json");
                File dirSrc = problemsFileSrc.getParentFile(); if (dirSrc!=null && !dirSrc.exists()) dirSrc.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileSrc, list);
                File problemsFileTarget = new File("target/public/JSON_files/problems.json");
                File dirTarget = problemsFileTarget.getParentFile(); if (dirTarget!=null && !dirTarget.exists()) dirTarget.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTarget, list);
                File problemsFileTargetClasses = new File("target/classes/public/JSON_files/problems.json");
                File dirTargetClasses = problemsFileTargetClasses.getParentFile(); if (dirTargetClasses!=null && !dirTargetClasses.exists()) dirTargetClasses.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTargetClasses, list);
            } catch (Exception e) { System.err.println("Failed to persist problems.json after approve: " + e.getMessage()); }

            // return success and newStatus (so frontend can update row locally)
            Map<String,Object> out = new HashMap<>(); out.put("success", true); out.put("id", id); out.put("userId", (target==null?null:target.getUsername())); out.put("newStatus", (target==null?"approved":target.getStatus().name())); ctx.json(out);
        });

        app.post("/submissions/:id/reject", ctx -> {
            String id = ctx.pathParam("id");
            ProblemForm target = null;
            for (ProblemForm p : problemList.getFormList()) {
                if (p.getId().equals(id)) { p.setStatus(EnumStatus.rejected); target = p; break; }
            }
            // notify resident (attach userId if found)
            try {
                Notification notif = new Notification("resident", "Demande refusée", "Votre demande a été rejetée");
                if (target != null && target.getUsername() != null) notif.setUserId(target.getUsername());
                notifications.add(notif);
                saveNotifications();
            } catch (Exception ignored) {}

            // persist updated problems list so static copies also reflect the change
            try {
                ObjectMapper problemsMapper = new ObjectMapper();
                java.util.List<ProblemForm> list = problemList.getFormList();
                File problemsFileSrc = new File("src/main/resources/public/JSON_files/problems.json");
                File dirSrc = problemsFileSrc.getParentFile(); if (dirSrc!=null && !dirSrc.exists()) dirSrc.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileSrc, list);
                File problemsFileTarget = new File("target/public/JSON_files/problems.json");
                File dirTarget = problemsFileTarget.getParentFile(); if (dirTarget!=null && !dirTarget.exists()) dirTarget.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTarget, list);
                File problemsFileTargetClasses = new File("target/classes/public/JSON_files/problems.json");
                File dirTargetClasses = problemsFileTargetClasses.getParentFile(); if (dirTargetClasses!=null && !dirTargetClasses.exists()) dirTargetClasses.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTargetClasses, list);
            } catch (Exception e) { System.err.println("Failed to persist problems.json after reject: " + e.getMessage()); }

            Map<String,Object> out = new HashMap<>(); out.put("success", true); out.put("id", id); out.put("userId", (target==null?null:target.getUsername())); out.put("newStatus", (target==null?"rejected":target.getStatus().name())); ctx.json(out);
        });

        app.post("/submissions/:id/request-modification", ctx -> {
            String id = ctx.pathParam("id");
            String message = ctx.formParam("message");
            ProblemForm target = null;
            for (ProblemForm p : problemList.getFormList()) {
                if (p.getId().equals(id)) { p.setStatus(EnumStatus.onHold); target = p; break; }
            }
            // notify resident with optional message (attach userId if found)
            try {
                String text = (message != null && !message.isEmpty()) ? ("Des modifications sont demandées: " + message) : "Des modifications sont demandées";
                Notification notif = new Notification("resident", "Modification demandée", text);
                if (target != null && target.getUsername() != null) notif.setUserId(target.getUsername());
                notifications.add(notif);
                saveNotifications();
            } catch (Exception ignored) {}

            // persist updated problems list so static copies also reflect the change
            try {
                ObjectMapper problemsMapper = new ObjectMapper();
                java.util.List<ProblemForm> list = problemList.getFormList();
                File problemsFileSrc = new File("src/main/resources/public/JSON_files/problems.json");
                File dirSrc = problemsFileSrc.getParentFile(); if (dirSrc!=null && !dirSrc.exists()) dirSrc.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileSrc, list);
                File problemsFileTarget = new File("target/public/JSON_files/problems.json");
                File dirTarget = problemsFileTarget.getParentFile(); if (dirTarget!=null && !dirTarget.exists()) dirTarget.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTarget, list);
                File problemsFileTargetClasses = new File("target/classes/public/JSON_files/problems.json");
                File dirTargetClasses = problemsFileTargetClasses.getParentFile(); if (dirTargetClasses!=null && !dirTargetClasses.exists()) dirTargetClasses.mkdirs();
                problemsMapper.writerWithDefaultPrettyPrinter().writeValue(problemsFileTargetClasses, list);
            } catch (Exception e) { System.err.println("Failed to persist problems.json after request-modification: " + e.getMessage()); }

            Map<String,Object> out = new HashMap<>(); out.put("success", true); out.put("id", id); out.put("userId", (target==null?null:target.getUsername())); out.put("newStatus", (target==null?"onHold":target.getStatus().name())); ctx.json(out);
        });

        ObjectMapper mapper = new ObjectMapper();
        // a seemingly complicated way to create the placeholders of problems from our json using jackson
        problemList.getFormList().addAll(mapper.readValue(new File("src/main/resources/public/JSON_files/problems.json"), new TypeReference<ArrayList<ProblemForm>>() {}));
        demandeList.getDemandList().addAll(mapper.readValue(new File("src/main/resources/public/JSON_files/demands.json"), new TypeReference<ArrayList<DemandForm>>() {}));


    }

    // Load notifications from JSON file if present
    private static void loadNotifications() {
        try {
            File f = new File(NOTIF_FILE);
            if (!f.exists()) return;
            List<Notification> loaded = NOTIF_MAPPER.readValue(f, new TypeReference<List<Notification>>() {});
            if (loaded != null) {
                notifications.clear();
                notifications.addAll(loaded);
            }
        } catch (Exception e) {
            System.err.println("Failed to load notifications: " + e.getMessage());
        }
    }

    // Save notifications to JSON file
    private static void saveNotifications() {
        try {
            File f = new File(NOTIF_FILE);
            File dir = f.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();
            NOTIF_MAPPER.writerWithDefaultPrettyPrinter().writeValue(f, notifications);
        } catch (Exception e) {
            System.err.println("Failed to save notifications: " + e.getMessage());
        }
    }

    public static void sendMessageToUI(String msg){
        messageQueue.add(msg);
    }

    public void showList(){

    }

}

/* private static void loadDemandeFromJson(){
        String jsonContent = new String(Files.readAllBytes(Paths.get(MTLjson)));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        JsonNode itemsArray = rootNode.path("records");

        for (int i=0; i < 5; i++){
            JsonNode item = itemsArray.get(i);
            String id = item.get("id").asText();
            String borough = item.get("borough").asText();
            EnumBoroughID enumBoroughID;
            switch (borough) {
                case "Anjou":
                    enumBoroughID = EnumBoroughID.anjou;
                case "Côte-des-Neiges—Notre-Dame-de-Grâce":
                    enumBoroughID = EnumBoroughID.coteDesNeigesNotreDameDeGrace;
                case "Le Plateau-Mont-Royal":
                    enumBoroughID = EnumBoroughID.lePlateauMontRoyal;
                case "Le Sud-Ouest":
                    enumBoroughID = EnumBoroughID.leSudOuest;
                case "Mercier–Hochelaga-Maisonneuve":
                    enumBoroughID = EnumBoroughID.mercierHochelagaMaisonneuve;
                case "Montréal-Nord":
                    enumBoroughID = EnumBoroughID.montrealNord;
                case "Outremont":
                    enumBoroughID = EnumBoroughID.outremont;
                case "Rivière-des-Prairies—Pointe-aux-Trembles":
                    enumBoroughID = EnumBoroughID.riviereDesPrairiesPointeAuxTrembles;
                case "Rosemont—La Petite-Patrie":
                    enumBoroughID = EnumBoroughID.rosemontLaPetitePatrie;
                case "Saint-Léonard":
                    enumBoroughID = EnumBoroughID.saintLeonard;
                case "Villeray—Saint-Michel—Parc-Extension":
                    enumBoroughID = EnumBoroughID.villeraySaintMichelParcExtension;
                case "Ville-Marie":
                    enumBoroughID = EnumBoroughID.villeMarie;
                default:
                    throw new IllegalArgumentException("Unknown borough: " + borough);
            }


        }

    }*/
