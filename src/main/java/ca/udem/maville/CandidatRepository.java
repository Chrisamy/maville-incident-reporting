package ca.udem.maville;

import java.util.ArrayList;

public class CandidatRepository {

    //Repository pour stocker les demandes envoyées par les prestataires
    //On utilise une instance globale

    private static CandidatRepository instance = null;
    private static ArrayList<PrestataireForm> demandList;

    private CandidatRepository() {
        demandList = new ArrayList<>();
    }

    public static synchronized CandidatRepository getInstance() {
        if (instance == null) {
            instance = new CandidatRepository();
        }
        return instance;
    }

    public ArrayList<PrestataireForm> getDemandList() {
        return demandList;
    }
    public static void addDemand(PrestataireForm form) {
        demandList.add(form);
    }
}
