package ca.udem.maville;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProblemRepositoryTest {

    @Test
    void testInstanceNotNull() {
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo);
    }

    @Test
    void getFormListReturnsList() {
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo.getFormList());
    }
}

