package ca.udem.maville;

import java.util.ArrayList;

public class AgentDemandFormHandler {

    protected static DemandForm getDemand(ArrayList<DemandForm> demandList, String idForm) {
        for (DemandForm d : demandList) {
            if (d.getId().equals(idForm)) {
                return d;
            }
        }
        return null;
    }

    public void RejectDemand(ArrayList<DemandForm> demandList, String id) {
        DemandForm demand = getDemand(demandList, id);
        demand.setStatus(EnumStatus.rejected);
        Server.sendMessageToUI("La demande " + id + "est refusée.");
    }

    public void AcceptDemand(ArrayList<DemandForm> demandList, String id) {
        DemandForm demand = getDemand(demandList, id);
        demand.setStatus(EnumStatus.approved);
        Server.sendMessageToUI("La demande " + id + "est acceptée.");
    }
}
