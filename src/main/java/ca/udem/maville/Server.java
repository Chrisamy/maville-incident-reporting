package ca.udem.maville;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Server {

    public static Javalin app;

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

        Desktop desktop = Desktop.getDesktop();
        try {

            //desktop.browse(new URI(String.format("http://localhost:%d", port)));
            // PLR TESTING : to open the page in resident directly
            //desktop.browse(new URI(String.format("http://localhost:7000/viewResident.html", port)));
            // PLR TESTING : to open the page in contractor directly
            //desktop.browse(new URI(String.format("http://localhost:7000/viewPrestataire.html", port)));
            // PLR TESTING : to open the page in agent directly
            desktop.browse(new URI(String.format("http://localhost:%d/viewAgent.html", port)));

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
                    ctx.res().getWriter().write("data: " + messageQueue.getFirst() + "\n\n");
                    ctx.res().getWriter().flush();
                    messageQueue.removeFirst();
                }
                Thread.sleep(1000); // chaque seconde on check pour un nouveaux message chaque seconde
            }
        });

        /**=============================================================================================================
         API MANIPULATION FOR THE RESIDENT
         =============================================================================================================*/

        app.post("/api/resident-log-in", ctx -> {
            currentResident = ctx.bodyAsClass(Resident.class);
            ctx.json(currentResident);
            System.out.println(currentResident.getUsername());
            // PLR TESTING: test added to see if the username gets added
            System.out.println(currentResident.getPassword());
            System.out.println("yeye end of log in info");
        });

        app.post("/api/resident-form-send", ctx -> {
            // logs
            String addr = ctx.formParam("address");
            String details = ctx.formParam("details");
            System.out.println("[SERVER] /api/resident-form-send received: address='" + addr + "' details='" + details + "'");
            System.out.println("[SERVER] currentResident object: " + (currentResident == null ? "null" : currentResident.getUsername()));

            ProblemForm problemForm = new ProblemForm(addr, currentResident == null ? "anonymous" : currentResident.getUsername(), details);
            System.out.println("[SERVER] created ProblemForm: " + problemForm);
            ctx.json(problemForm);

            if (currentResident != null) {
                currentResident.submitForm(problemForm);
            } else {
                ProblemRepository.addForm(problemForm);
                System.out.println("[SERVER] No current resident; form saved in repository without association.");
            }
        });

        /**=============================================================================================================
        API MANIPULATION FOR THE AGENT
         =============================================================================================================*/

        app.post("/api/agent-refuse-problem", ctx -> {

            String formId = ctx.formParam("id");
            // N.B.: the status change is implicit in the methode RefuseProblem
            problemFormHandler.RefuseProblem(problemList.getFormList(), formId);
        });

        app.post("/api/agent-accept-problem", ctx -> {
            String formId = ctx.formParam("id");
            String workType = ctx.formParam("workType");
            String priority = ctx.formParam("priority");

            // Get the EnumPriority associated with the string
            EnumWorkType newWorkType = EnumWorkType.valueOf(workType);
            EnumPriority newPriority = EnumPriority.valueOf(priority);

            problemFormHandler.AcceptProblem(problemList.getFormList(), formId, newWorkType,newPriority);
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

        ObjectMapper mapper = new ObjectMapper();
        // a seemingly complicated way to create the placeholders of problems from our json using jackson
        problemList.getFormList().addAll(mapper.readValue(new File("src/main/resources/public/JSON_files/problems.json"), new TypeReference<ArrayList<ProblemForm>>() {}));
        demandeList.getDemandList().addAll(mapper.readValue(new File("src/main/resources/public/JSON_files/demands.json"), new TypeReference<ArrayList<DemandForm>>() {}));


    }

    public static void sendMessageToUI(String msg){
        messageQueue.add(msg);
    }

    public void showList(){

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


}