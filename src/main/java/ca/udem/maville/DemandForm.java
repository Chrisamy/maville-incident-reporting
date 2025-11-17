package ca.udem.maville;

public class DemandForm {
    Prestataire prestataire;
    int companyNumber;
    String projectTitle;
    EnumWorkType workType;
    String location;
    String description;
    String startDate;
    String endDate;
    String id;
    float costEstimate;

    public DemandForm(Prestataire prestataire, int companyNumber, String projectTitle, EnumWorkType workType,
                      String location, String description, String startDate, String endDate, String id, float costEstimate) {
        this.prestataire = prestataire;
        this.companyNumber = companyNumber;
        this.projectTitle = projectTitle;
        this.workType = workType;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
        this.costEstimate = costEstimate;

    }

    //Getters

    public Prestataire getPrestataire() {
        return prestataire;
    }
    public int getCompanyNumber() { return prestataire.getCompanyNumber(); } // PLR VERIF not sure pour cela
    public String getProjectTitle() {
        return projectTitle;
    }
    public EnumWorkType getWorkType() { return workType; }
    public String getLocation() {
        return location;
    }
    public String getDescription() {
        return description;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public String getId() {
        return id;
    }
    public float getCostEstimate() {return costEstimate;}

    //Setters

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }
    public void setCompanyNumber(int companyNumber) {   this.companyNumber = companyNumber; }
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }
    public void setWorkType(EnumWorkType workType) {
        this.workType = workType;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setCostEstimate(float costEstimate) {this.costEstimate = costEstimate;}
}
