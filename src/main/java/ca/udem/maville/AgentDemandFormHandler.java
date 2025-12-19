package ca.udem.maville;

import java.util.ArrayList;

public class AgentDemandFormHandler {

    //Classe qui regroupe les méthodes de traitement de l'agent pour les demandes de travaux soumises
    //par les prestataires

    //Méthode pour obtenir une demande par son id

    protected static DemandForm getDemand(ArrayList<DemandForm> demandList, String idForm) {
        for (DemandForm d : demandList) {
            if (d.getId().equals(idForm)) {
                return d;
            }
        }
        return null;
    }

    //Méthode pour rejeter une demande

    public void RejectDemand(ArrayList<DemandForm> demandList, String id) {
        DemandForm demand = getDemand(demandList, id);
        demand.setStatus(EnumStatus.rejected);
        Server.sendMessageToUI("La demande " + id + "est refusée.");
    }

    //Méthode pour accepter une demande

    public void AcceptDemand(ArrayList<DemandForm> demandList, String id) {
        DemandForm demand = getDemand(demandList, id);
        demand.setStatus(EnumStatus.approved);
        Server.sendMessageToUI("La demande " + id + "est acceptée.");
    }
}
