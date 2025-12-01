package ca.udem.maville;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AgentProblemFormHandlerTest {

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
}

