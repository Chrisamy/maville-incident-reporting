package ca.udem.maville;

public class ProblemForm {
    EnumWorkType WorkType;
    String location;
    String username;
    EnumPriority enumPriority;
    String id;
    String descritption;
    EnumStatus enumStatus;

    //Constructor

    public ProblemForm(String location, String username, String descritption) {
        this.WorkType = EnumWorkType.notDefined;
        this.location = location;
        this.username = username;
        this.enumPriority = EnumPriority.notAssigned;
        this.id = IdGenerator.generateID();
        this.descritption = descritption;
        this.enumStatus = EnumStatus.waitingForApproval;
    }

    //Getters

    public EnumWorkType getWorkType() {
        return WorkType;
    }
    public String getLocation() {
        return location;
    }
    public String getUsername() {
        return username;
    }
    public EnumPriority getPriority() {
        return enumPriority;
    }
    public String getId() {
        return id;
    }
    public String getDescritption() {
        return descritption;
    }
    public EnumStatus getStatus() {
        return enumStatus;
    }

    //Setters

    public void setWorkType(EnumWorkType WorkType) {
        this.WorkType = WorkType;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPriority(EnumPriority enumPriority) {
        this.enumPriority = enumPriority;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setDescritption(String descritption) {
        this.descritption = descritption;
    }
    public void setStatus(EnumStatus enumStatus) {
        this.enumStatus = enumStatus;
    }
}
