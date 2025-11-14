package ca.udem.maville;

public class FormResident {
    EnumWorkType WorkType;
    String location;
    String username;
    Priority priority;
    String id;
    String descritption;
    Status status;

    //Constructor

    public FormResident(String location, String username, String descritption) {
        this.WorkType = EnumWorkType.notDefined;
        this.location = location;
        this.username = username;
        this.priority = Priority.notAssigned;
        this.id = IdGenerator.generateID();
        this.descritption = descritption;
        this.status = Status.waitingForApproval;
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
    public Priority getPriority() {
        return priority;
    }
    public String getId() {
        return id;
    }
    public String getDescritption() {
        return descritption;
    }
    public Status getStatus() {
        return status;
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
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setDescritption(String descritption) {
        this.descritption = descritption;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}

