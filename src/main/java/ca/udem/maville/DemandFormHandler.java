package ca.udem.maville;

public class DemandFormHandler {


    public void submitDemand(Prestataire prestataire, int companyNumber, String projectTitle, EnumWorkType workType,
                             String location, String description, String startDate, String endDate, String id, float costEstimate) {
        DemandForm form = new DemandForm(prestataire, companyNumber, projectTitle, workType, location,
                description, startDate, endDate, id, costEstimate);
        DemandRepository.addForm(form);
    }

}
