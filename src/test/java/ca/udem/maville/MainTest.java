package ca.udem.maville;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static ca.udem.maville.AgentProblemFormHandler.getProblem;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    public void testsAreWorking() {
        assertTrue(true);
    }

    // --- IdGenerator ---

    // ID cannot be null
    @Test
    void testGenerateID_notNull() {
        String id = IdGenerator.generateID();
        assertNotNull(id, "ID should not be null");
    }

    // ID can't be empty
    @Test
    void testGenerateID_notEmpty() {
        String id = IdGenerator.generateID();
        assertFalse(id.isEmpty(), "ID should not be empty");
    }

    // ID needs to be in a valid UUID format
    @Test
    void testGenerateID_validUUIDFormat() {
        String id = IdGenerator.generateID();
        assertDoesNotThrow(() -> {
            UUID.fromString(id); // Doit réussir si id est un vrai UUID
        }, "ID should be valid");
    }

    // Two generated IDs should not be equal
    @Test
    void generateIdProducesNonEmptyUniqueValues() {
        String a = IdGenerator.generateID();
        String b = IdGenerator.generateID();
        assertNotNull(a);
        assertNotNull(b);
        assertFalse(a.isEmpty());
        assertFalse(b.isEmpty());
        assertNotEquals(a, b, "two generated IDs should not be equal");
    }

    // --- getProblem ---

    // Tests if the problem found matches the id
    @Test
    void testGetProblemFound() {
        ArrayList<ProblemForm> list = new ArrayList<>();

        // Formulaires créés : l'ID est généré automatiquement
        ProblemForm p1 = new ProblemForm(null, null, null);
        ProblemForm p2 = new ProblemForm(null, null, null);

        list.add(p1);
        list.add(p2);


        String id = p2.getId();

        ProblemForm result = getProblem(list, id);

        assertNotNull(result, "corresponding problem should not be null");
        assertEquals(id, result.getId(), "ID should match");
        assertSame(p2, result, "returned problem should be the same (p2)");
    }

    // Function should return null if the id does not exist
    @Test
    void testGetProblemNotFound() {
        ArrayList<ProblemForm> list = new ArrayList<>();

        ProblemForm p1 = new ProblemForm(null, null, null);
        ProblemForm p2 = new ProblemForm(null, null, null);
        list.add(p1);
        list.add(p2);


        String nonExistingId = "id that does not exist";

        ProblemForm result = getProblem(list, nonExistingId);

        assertNull(result, "If no id matches the problem, function should return null");
    }

    // Trying to get a problem in an empty list should return null
    @Test
    void testGetProblemEmptyList() {
        ArrayList<ProblemForm> list = new ArrayList<>();

        ProblemForm result = getProblem(list, "id (can be whatever it doesn't matter)");

        assertNull(result, "An empty list return null");
    }

    // --- ProblemForm ---

    // Constructor with parameters sets fields correctly
    @Test
    void constructorSetsFieldsProblemForm() {
        ProblemForm test = new ProblemForm("123 Rue", "bobby", "j'ai trouvé un trou");
        assertEquals("123 Rue", test.getLocation());
        assertEquals("bobby", test.getUsername());
        assertEquals("j'ai trouvé un trou", test.getDescription());
        assertNotNull(test.getId());
    }

    // Default constructor initializes default values
    @Test
    void defaultConstructorInitializesDefaults() {
        ProblemForm test = new ProblemForm();
        assertNotNull(test.getId(), "id should be generated");
        assertEquals(EnumWorkType.notDefined, test.getWorkType(), "default workType should be notDefined");
        assertEquals(EnumPriority.notAssigned, test.getPriority(), "default priority should be notAssigned");
        assertEquals(EnumStatus.waitingForApproval, test.getStatus(), "default status should be waitingForApproval");
    }

    // --- ProblemRepository ---

    // singleton instance is not null
    @Test
    void testInstanceNotNull() {
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo);
    }

    // getFormList returns a non-null list
    @Test
    void getFormListReturnsList() {
        // the repository exposes a list (not null)
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo.getFormList());
    }


    // --- AgentProblemFormHandler ---

    // AcceptProblem updates workType, priority, and status
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
