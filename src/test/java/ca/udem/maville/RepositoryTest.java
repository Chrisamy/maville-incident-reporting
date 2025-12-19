package ca.udem.maville;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    //Test qui vérifie que l'instance du ProblemRepository n'est pas null

    @Test
    void testInstanceOfProblemRepoNotNull() {
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo);
    }

    //Test qui vérifie que la liste du ProblemRepository n'est pas null

    @Test
    void getFormListReturnsList() {
        ProblemRepository repo = ProblemRepository.getInstance();
        assertNotNull(repo.getFormList());
    }

    //Test qui vérifie que l'instance du DemandRepository n'est pas null

    @Test
    void testInstanceOfDemandRepoNotNull() {
        DemandRepository repo = DemandRepository.getInstance();
        assertNotNull(repo);
    }

    //Test qui vérifie que la liste du DemandRepository n'est pas null

    @Test
    void getDemandListReturnsList() {
        DemandRepository repo = DemandRepository.getInstance();
        assertNotNull(repo.getDemandList());
    }
}
