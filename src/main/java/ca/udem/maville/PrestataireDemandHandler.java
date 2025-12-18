package ca.udem.maville;

import java.util.ArrayList;

public class PrestataireDemandHandler {

    protected static DemandForm getDemand(ArrayList<DemandForm> DemandList, String id) {
        for (DemandForm demand : DemandList) {
            if (demand.getId().equals(id)) {
                return demand;
            }
        }
        return null;
    }

    public void changeStartDate(ArrayList<DemandForm> DemandList, String id, String newStartDate) {
        DemandForm demand = getDemand(DemandList, id);
        demand.setStartDate(newStartDate);
        Server.sendMessageToUI("La date de début du projet a été changée au : " + newStartDate);

    }

    public void changeEndDate(ArrayList<DemandForm> DemandList, String id, String newEndDate) {
        DemandForm demand = getDemand(DemandList, id);
        demand.setEndDate(newEndDate);
        Server.sendMessageToUI("La date de fin du projet a été changée au : " + newEndDate);
    }

    public void changeStatus(ArrayList<DemandForm> DemandList, String id, EnumStatus newStatus) {
        DemandForm demand = getDemand(DemandList, id);
        demand.setStatus(newStatus);
        Server.sendMessageToUI("Le statut du projet a été modifié au statut suivant : " + newStatus.toString());
    }

    public void changeCostEstimate(ArrayList<DemandForm> DemandList, String id, double newCostEstimate) {
        DemandForm demand = getDemand(DemandList, id);
        demand.setCostEstimate(newCostEstimate);
        Server.sendMessageToUI("Le nouveau coût estimé du projet est fixé à : " + newCostEstimate);
    }

}
