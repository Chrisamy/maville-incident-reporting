package ca.udem.maville;

import java.util.ArrayList;

public class DemandRepository {

    //Pour les demandes des prestataires

    static ArrayList<FormPrestataire> DemandList = new ArrayList<FormPrestataire>();

    public ArrayList<FormPrestataire> getAllForms() {
        return DemandList;
    }

    public static void addForm(FormPrestataire form) {
        DemandList.add(form);
    }
}
