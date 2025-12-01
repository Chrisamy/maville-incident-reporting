package ca.udem.maville;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProblemFormTest {

    @Test
    void constructorSetsFieldsProblemForm() {
        ProblemForm test = new ProblemForm("123 Rue", "bobby", "j'ai trouvé un trou");
        assertEquals("123 Rue", test.getLocation());
        assertEquals("bobby", test.getUsername());
        assertEquals("j'ai trouvé un trou", test.getDescription());
        assertNotNull(test.getId());
    }

    @Test
    void defaultConstructorInitializesDefaults() {
        ProblemForm test = new ProblemForm();
        assertNotNull(test.getId(), "id should be generated");
        assertEquals(EnumWorkType.notDefined, test.getWorkType(), "default workType should be notDefined");
        assertEquals(EnumPriority.notAssigned, test.getPriority(), "default priority should be notAssigned");
        assertEquals(EnumStatus.waitingForApproval, test.getStatus(), "default status should be waitingForApproval");
    }
}

