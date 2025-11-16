package ca.udem.maville;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class DemandRepository {

    //Pour les demandes des prestataires

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
    public static void addForm(DemandForm form) {
        demandList.add(form);
    }
}
