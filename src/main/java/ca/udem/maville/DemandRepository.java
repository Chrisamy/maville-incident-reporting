package ca.udem.maville;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class DemandRepository {

    //Repository pour stocker les demandes envoyées par les prestataires
    //On utilise une instance globale

    private static DemandRepository instance = null;
    private static ArrayList<DemandForm> demandList;

    private DemandRepository() {
        demandList = new ArrayList<>();
    }

    public static synchronized DemandRepository getInstance() {
        if (instance == null) {
            instance = new DemandRepository();
        }
        return instance;
    }

    public ArrayList<DemandForm> getDemandList() {
        return demandList;
    }
    public static void addDemand(DemandForm form) {
        demandList.add(form);
    }
}
