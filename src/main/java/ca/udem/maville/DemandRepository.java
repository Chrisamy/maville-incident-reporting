package ca.udem.maville;

import java.util.ArrayList;

public class DemandRepository {

    //Pour les demandes des prestataires

    private static DemandRepository instance = null;
    public static ArrayList<DemandForm> DemandList = new ArrayList<DemandForm>();

    private DemandRepository() {
        DemandList = new ArrayList<>();
    }

    public static synchronized DemandRepository getInstance() {
        if (instance == null) {
            instance = new DemandRepository();
        }
        return instance;
    }


    public ArrayList<DemandForm> getAllForms() {
        return DemandList;
    }

    public static void addForm(DemandForm form) {
        DemandList.add(form);
    }
}
