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

    //ID cannot be null

    @Test
    void testGenerateID_notNull() {
        String id = IdGenerator.generateID();
        assertNotNull(id, "ID should not be null");
    }

    //ID can't be empty

    @Test
    void testGenerateID_notEmpty() {
        String id = IdGenerator.generateID();
        assertFalse(id.isEmpty(), "ID should not be empty");
    }

    //ID needs to be in a valid UUID format

    @Test
    void testGenerateID_validUUIDFormat() {
        String id = IdGenerator.generateID();
        assertDoesNotThrow(() -> {
            UUID.fromString(id); // Doit réussir si id est un vrai UUID
        }, "ID should be valid");
    }

    //Tests if the problem found matches the id

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

    //Function should return null if the id does not exist

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

    //Trying to get a problem in an empty list should return null

    @Test
    void testGetProblemEmptyList() {
        ArrayList<ProblemForm> list = new ArrayList<>();

        ProblemForm result = getProblem(list, "id (can be whatever it doesn't matter)");

        assertNull(result, "An empty list return null");
    }


}
