package ca.udem.maville;

import java.util.ArrayList;

public class AgentProblemFormHandler {

    //Accède à un formulaire via son id

    protected static ProblemForm getProblem(ArrayList<ProblemForm> FormList, String idForm) {
        for (ProblemForm f : FormList) {
            if (f.getId().equals(idForm)) {
                return f;
            }
        }
        return null;
    }

    //Au cas où la priorité d'un problème change

    private void AssignPriority(ArrayList<ProblemForm> FormList, String idForm, EnumPriority enumPriority) {
        ProblemForm f = getProblem(FormList, idForm);
        if (f != null) {
            f.setPriority(enumPriority);
        }
    }

    //Méthode pour accepter un projet

    private void AcceptProject(ArrayList<ProblemForm> formList, String id, EnumPriority newEnumPriority, EnumWorkType newWorkType) {
        ProblemForm form = getProblem(formList, id);
        form.setPriority(newEnumPriority);
        form.setWorkType(newWorkType);
        form.setStatus(EnumStatus.approved);
    }
}
