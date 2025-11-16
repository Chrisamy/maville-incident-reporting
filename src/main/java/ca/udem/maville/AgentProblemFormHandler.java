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

    /**=============================================================================================================
     PROBLEM FORM MANIPULATION
     =============================================================================================================*/

    public void RefuseProblem(ArrayList<ProblemForm> formList, String id) {
        ProblemForm form = getProblem(formList, id);
        form.setStatus(EnumStatus.rejected);
        Server.sendMessageToUI("Le projet " + id + "est refusé.");
    }

    //Méthode pour accepter un projet
    // PLR verif: changed from private to public
    public void AcceptProblem(ArrayList<ProblemForm> formList, String id, EnumWorkType newWorkType, EnumPriority newEnumPriority) {
        ProblemForm form = getProblem(formList, id);
        form.setWorkType(newWorkType);
        form.setPriority(newEnumPriority);
        form.setStatus(EnumStatus.approved);
        Server.sendMessageToUI("Le projet " + id + "est accepté.");
    }

    //Au cas où la priorité d'un problème change
    // PLR verif: changed from private to public
    public void AssignProblemPriority(ArrayList<ProblemForm> FormList, String idForm, EnumPriority enumPriority) {
        ProblemForm f = getProblem(FormList, idForm);
        if (f != null) {
            f.setPriority(enumPriority);
            Server.sendMessageToUI("Le projet " + idForm + " a eu sa priorité changé pour " + enumPriority);
        }
        // PLR verif : do we need to throw an exeption if the form is not found?
    }


    // Method to update the project from example "inProgress" to "finished"
    // PLR verif: changed from private to public
    public void UpdateProjectStatus(ArrayList<ProblemForm> formList, String id, EnumStatus newStatus) {
        ProblemForm form = getProblem(formList, id);
        form.setStatus(newStatus);
        Server.sendMessageToUI("Le status du projet " + id + " a eu sa priorité changé pour " + newStatus);
    }
}
