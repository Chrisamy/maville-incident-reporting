package ca.udem.maville;

import java.util.ArrayList;

public class priorityHandler {

    private FormResident getProblem(ArrayList<FormResident> FormList, String idForm) {
        for (FormResident f : FormList) {
            if (f.getId().equals(idForm)) {
                return f;
            }
        }
        return null;
    }

    private void AssignPriority(ArrayList<FormResident> FormList, String idForm, Priority priority) {
        FormResident f = getProblem(FormList, idForm);
        if (f != null) {
            f.setPriority(priority);
        }
    }
}
