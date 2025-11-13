package ca.udem.maville;

import java.util.ArrayList;

public class DemandRepository {
    ArrayList<FormPrestataire> DemandList = new ArrayList<FormPrestataire>();

    public ArrayList<FormPrestataire> getAllForms() {
        return DemandList;
    }

    public void addForm(FormPrestataire form) {
        DemandList.add(form);
    }
}
