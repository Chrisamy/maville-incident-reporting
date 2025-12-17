package ca.udem.maville;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProblemRepositoryTest {

    //Test qui vérifie que l'instance du ProblemRepository n'est pas null

    @Test
    void testInstanceNotNull() {
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo);
    }

    //Test qui vérifie que la liste du ProblemRepository n'est pas null

    @Test
    void getFormListReturnsList() {
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo.getFormList());
    }
}

