package ca.udem.maville;

import java.util.ArrayList;

public class AgentDemandFormHandler {

    protected static DemandForm getDemand(ArrayList<DemandForm> FormList, String idForm) {
        for (DemandForm d : FormList) {
            if (d.getId().equals(idForm)) {
                return d;
            }
        }
        return null;
    }


    public void AcceptDemand(boolean isAccepted){

    }

    public void RefuseDemand(ArrayList<DemandForm> formList, String id) {


    }
}
