package ca.udem.maville;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) //to ignore the _id property
public class PrestataireForm {
    @JsonIgnore

    private String id;
    private String projectTitle;
    private EnumWorkType workType;
    private String location;
    private String description;
    private String startDate;
    private String endDate;
    private double costEstimate;
    private EnumStatus status;

    public PrestataireForm(String projectTitle, EnumWorkType workType, String location, String description, String startDate,
                           String endDate, double costEstimate) {
        this.id = IdGenerator.generateID();
        this.projectTitle = projectTitle;
        this.workType = workType;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.costEstimate = costEstimate;
        this.status = EnumStatus.waitingForApproval;
    }

    //Getters

    public String getId() {
        return id;
    }
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
    public double getCostEstimate() {return costEstimate;}
    public EnumStatus getStatus() {
        return status;
    }

    //Setters

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
    public void setCostEstimate(double costEstimate) {this.costEstimate = costEstimate;}
    public void setStatus(EnumStatus status) {
        this.status = status;
    }
}
