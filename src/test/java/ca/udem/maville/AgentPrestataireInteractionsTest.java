package ca.udem.maville;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class AgentPrestataireInteractionsTest {

    //Test qui vérifie que le statut de la demande est bien mis à approved après la validation de celle-ci

    @Test
    public void acceptDemandSetsStatusToApproved() {
        AgentPrestataireInteractions handler = new AgentPrestataireInteractions();
        ArrayList<PrestataireForm> demandList = new ArrayList<>();
        PrestataireForm demand = new PrestataireForm("titre", EnumWorkType.RoadWork, "chez moi", "demande de projet pour problème x", "12 novembre 2025",
                "7 février 2026", 56789.67);
        demandList.add(demand);
        handler.AcceptDemand(demandList, demand.getId());

        assertEquals(EnumStatus.approved, demand.getStatus());
    }

    //Test qui vérifie que le statut de la demande est bien mis à rejected après le refus de celle-ci

    @Test
    public void rejectDemandSetsStatusToRejected() {
        AgentPrestataireInteractions handler = new AgentPrestataireInteractions();
        ArrayList<PrestataireForm> demandList = new ArrayList<>();
        PrestataireForm demand = new PrestataireForm("titre", EnumWorkType.RoadWork, "quelque part", "demande de projet pour problème x", "14 septembre 2025",
                "12 mars 2026", 39021.15);
        demandList.add(demand);
        handler.RejectDemand(demandList, demand.getId());

        assertEquals(EnumStatus.rejected, demand.getStatus());
    }

    //Test qui vérifie que le message envoyé au serveur est le bon après la validation de la demande

    @Test
    void acceptDemandSendsCorrectMessage() {
        AgentPrestataireInteractions handler = new AgentPrestataireInteractions();
        ArrayList<PrestataireForm> demandList = new ArrayList<>();
        PrestataireForm demand = new PrestataireForm("titre", EnumWorkType.RoadWork, "chez moi", "demande de projet pour problème x", "12 novembre 2025",
                "7 février 2026", 56789.67);
        demandList.add(demand);

        try (MockedStatic<Server> serverMock = Mockito.mockStatic(Server.class)) {

            handler.AcceptDemand(demandList, demand.getId());

            serverMock.verify(() -> {
                        Server.sendMessageToUI("La demande " + demand.getId() + "est acceptée.");
                    }
            );
        }
    }

    //Test qui vérifie que le message envoyé au serveur est le bon après le refus de la demande

    @Test
    void rejectDemandSendsCorrectMessage() {
        AgentPrestataireInteractions handler = new AgentPrestataireInteractions();
        ArrayList<PrestataireForm> demandList = new ArrayList<>();
        PrestataireForm demand = new PrestataireForm("titre", EnumWorkType.RoadWork, "quelque part", "demande de projet pour problème x", "14 septembre 2025",
                "12 mars 2026", 39021.15);
        demandList.add(demand);

        try (MockedStatic<Server> serverMock = Mockito.mockStatic(Server.class)) {

            handler.RejectDemand(demandList, demand.getId());

            serverMock.verify(() -> {
                        Server.sendMessageToUI("La demande " + demand.getId() + "est refusée.");
                    }
            );
        }
    }

}
