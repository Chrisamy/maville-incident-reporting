package ca.udem.maville;

public class ApprovedProject extends DemandForm {

    private EnumStatus projectStatus;

    public ApprovedProject(Prestataire prestataire, int companyNumber, String projectTitle, EnumWorkType workType, String location, String description, String startDate, String endDate, String id, float costEstimate) {
        super(prestataire, companyNumber, projectTitle, workType, location, description, startDate, endDate, id, costEstimate);
    }

    // Setters
    public void setProjectDescription(String description) {
        this.description = description;
    }

    public void setProjectEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setProjectStatus(EnumStatus status) {
        this.projectStatus = status;
    }
}
