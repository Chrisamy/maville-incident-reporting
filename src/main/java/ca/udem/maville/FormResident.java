package ca.udem.maville;

public class FormResident {
    EnumWorkType WorkType;
    String location;
    String username;
    Priority priority = Priority.notAssigned;
    String id;
    String descritption;

    public void receiveForm(){

    }

    //Constructor

    public FormResident(String location, String username, String descritption) {
        this.WorkType = EnumWorkType.notDefined;
        this.location = location;
        this.username = username;
        this.id = "";
        this.descritption = descritption;
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
}
