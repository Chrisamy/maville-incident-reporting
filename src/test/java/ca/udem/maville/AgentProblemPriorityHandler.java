package ca.udem.maville;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AgentProblemPriorityHandler {

    //Test qui vérifie que la priorité d'un problème est changée lorsque désiré

    @Test
    public void assignNewPriorityChangesProblemPriority() {
        AgentProblemFormHandler handler = new AgentProblemFormHandler();
        ArrayList<ProblemForm> list = new ArrayList<>();
        ProblemForm problem = new ProblemForm("1234 rue chez moi", "Jean", "besion d'aide pour qqch");
        list.add(problem);

        handler.AssignProblemPriority(list, problem.getId(), EnumPriority.low);

        assertEquals(EnumPriority.low, problem.getPriority());

    }

    //Test qui vérifie que le changement de priorité d'un problème n'affecte pas les autres problèmes de la liste

    @Test
    public void assignNewPriorityDoesNotAffectOtherProblems() {
        AgentProblemFormHandler handler = new AgentProblemFormHandler();
        ArrayList<ProblemForm> list = new ArrayList<>();
        ProblemForm problem1 = new ProblemForm("chez moi", "Jean", "besion d'aide pour qqch");
        ProblemForm problem2 = new ProblemForm("chez toi", "Pierre", "besion d'aide pour qqch");
        list.add(problem1);
        list.add(problem2);

        handler.AssignProblemPriority(list, problem1.getId(), EnumPriority.low);
        assertEquals(EnumPriority.low, problem1.getPriority());
        assertEquals(EnumPriority.notAssigned, problem2.getPriority());

    }

    //Test qui vérifie que le changement de priorité envoie le bon message au serveur

    @Test
    public void assignPrioritySendsCorrectMessageToServer() {
        AgentProblemFormHandler handler = new AgentProblemFormHandler();
        ArrayList<ProblemForm> list = new ArrayList<>();
        ProblemForm p = new ProblemForm("loc", "username", "desc");
        list.add(p);

        try (MockedStatic<Server> serverMock = Mockito.mockStatic(Server.class)) {

            handler.AssignProblemPriority(list, p.getId(), EnumPriority.high);

            serverMock.verify(() -> {
                Server.sendMessageToUI("Le projet " + p.getId() + " a eu sa priorité changé pour " + EnumPriority.high);
            });

        }

    }
}
