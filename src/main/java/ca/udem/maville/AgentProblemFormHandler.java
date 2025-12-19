package ca.udem.maville;

import java.util.ArrayList;

public class AgentProblemFormHandler {

    //Classe qui regroupe les méthodes de traitement de l'agent pour les formulaires soumis par les résidents

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

    //Méthode pour refuser un formulaire (refuser le problème)

    public void RefuseProblem(ArrayList<ProblemForm> formList, String id) {
        ProblemForm form = getProblem(formList, id);
        form.setStatus(EnumStatus.rejected);
        Server.sendMessageToUI("Le projet " + id + "est refusé.");
    }

    //Méthode pour accepter un formulaire (accepter le problème)

    public void AcceptProblem(ArrayList<ProblemForm> formList, String id, EnumWorkType newWorkType, EnumPriority newEnumPriority) {
        ProblemForm form = getProblem(formList, id);
        form.setWorkType(newWorkType);
        form.setPriority(newEnumPriority);
        form.setStatus(EnumStatus.approved);
        Server.sendMessageToUI("Le projet " + id + "est accepté.");
    }

    //Méthode pour changer la priorité d'un problème

    public void AssignProblemPriority(ArrayList<ProblemForm> FormList, String idForm, EnumPriority enumPriority) {
        ProblemForm f = getProblem(FormList, idForm);
        System.out.println(f);
        if (f != null) {
            f.setPriority(enumPriority);
            Server.sendMessageToUI("Le projet " + idForm + " a eu sa priorité changé pour " + enumPriority);
            System.out.println(f);
        }
    }

}
