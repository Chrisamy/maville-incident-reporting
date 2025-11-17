package ca.udem.maville;

import java.util.ArrayList;

public class PrestataireUpdateProject {

    protected static ApprovedProject getProject(ArrayList<ApprovedProject> ProjectList, String idForm) {
        for (ApprovedProject p : ProjectList) {
            if (p.getId().equals(idForm)) {
                return p;
            }
        }
        return null;
    }

    public void UpdateProjectDescription(){
       // ApprovedProject project = getProject();

    }
    public void UpdateProjectEndDate(){

    }
    public void UpdateProjectStatus(){

    }
}
