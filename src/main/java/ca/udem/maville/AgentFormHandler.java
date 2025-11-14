package ca.udem.maville;

import java.util.ArrayList;

public class AgentFormHandler {

    //Accède à un formulaire via son id

    protected static FormResident getProblem(ArrayList<FormResident> FormList, String idForm) {
        for (FormResident f : FormList) {
            if (f.getId().equals(idForm)) {
                return f;
            }
        }
        return null;
    }

    //Au cas où la priorité d'un problème change

    private void AssignPriority(ArrayList<FormResident> FormList, String idForm, Priority priority) {
        FormResident f = getProblem(FormList, idForm);
        if (f != null) {
            f.setPriority(priority);
        }
    }

    //Méthode pour accepter un projet

    private void AcceptProject(ArrayList<FormResident> formList, String id, Priority newPriority, EnumWorkType newWorkType) {
        FormResident form = getProblem(formList, id);
        form.setPriority(newPriority);
        form.setWorkType(newWorkType);
        form.setStatus(Status.approved);
    }
}
