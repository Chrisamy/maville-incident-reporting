package ca.udem.maville;

import java.util.ArrayList;

public class DemandRepository {
    ArrayList<FormPrestataire> DemandList = new ArrayList<FormPrestataire>();

    private static DemandRepository instance = null;
    public ArrayList<FormResident> FormList;

    private DemandRepository() {
        DemandList = new ArrayList<>();
    }

    public static synchronized DemandRepository getInstance() {
        if (instance == null) {
            instance = new DemandRepository();
        }
        return instance;
    }


    public ArrayList<FormPrestataire> getAllForms() {
        return DemandList;
    }

    public void addForm(FormPrestataire form) {
        DemandList.add(form);
    }
}
