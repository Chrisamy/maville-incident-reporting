package ca.udem.maville;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AgentProblemFormHandlerTest {

    //Test pour vérifier que les attributs du problème sont bien attribués

    @Test
    void acceptProblemSetsFieldsAndStatus() {
        AgentProblemFormHandler handler = new AgentProblemFormHandler();
        ArrayList<ProblemForm> list = new ArrayList<>();
        ProblemForm form = new ProblemForm("123 rue OursBrun", "petit ours brun", "les abeilles sont en feu");
        list.add(form);

        handler.AcceptProblem(list, form.getId(), EnumWorkType.RoadWork, EnumPriority.high);

        assertEquals(EnumWorkType.RoadWork, form.getWorkType(), "workType should be updated");
        assertEquals(EnumPriority.high, form.getPriority(), "priority should be updated");
        assertEquals(EnumStatus.approved, form.getStatus(), "status should be set to approved");
    }

    //Test pour vérifier que le statut d'un problème refusé est bien mis à rejected

    @Test
    void rejectProblemSetsStatus() {
        AgentProblemFormHandler handler = new AgentProblemFormHandler();
        ArrayList<ProblemForm> list = new ArrayList<>();
        ProblemForm form = new ProblemForm("45 rue tkt", "gars mystérieux", "y'a un trou dans ma toile de piscine");
        list.add(form);

        handler.RefuseProblem(list, form.getId());
        assertEquals(EnumStatus.rejected, form.getStatus(), "status should be set to rejected");
    }

    //Test qui vérifie que le bon message est envoyé au serveur lorsqu'un problème est refusé

    @Test
    void refuseProblemSendsCorrectMessage() {
        AgentProblemFormHandler handler = new AgentProblemFormHandler();
        ArrayList<ProblemForm> list = new ArrayList<>();
        ProblemForm p = new ProblemForm("loc", "username", "desc");
        list.add(p);

        try (MockedStatic<Server> serverMock = Mockito.mockStatic(Server.class)) {

            handler.RefuseProblem(list, p.getId());

            serverMock.verify(() -> {
                        Server.sendMessageToUI("Le projet " + p.getId() + "est refusé.");
                    }
            );
        }
    }

    //Test qui vérifie que le bon message est envoyé lorsqu'un problème est accepté

    @Test
    void acceptProblemSendsCorrectMessage() {
        AgentProblemFormHandler handler = new AgentProblemFormHandler();
        ArrayList<ProblemForm> list = new ArrayList<>();
        ProblemForm p = new ProblemForm("loc", "username", "desc");
        list.add(p);

        try (MockedStatic<Server> serverMock = Mockito.mockStatic(Server.class)) {

            handler.AcceptProblem(list, p.getId(), EnumWorkType.UrbanMaintenance, EnumPriority.high);

            serverMock.verify(() -> {
                Server.sendMessageToUI("Le projet " + p.getId() + "est accepté.");
            });
        }
    }


}

