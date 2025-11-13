package ca.udem.maville;

public class FormResident {

    public String location;
    public String description;
    public String priority;

    private String id;
    private EnumWorkType WorkType;
    private String username;
    private PriorityEnum priorityEnum;

    public void receiveForm(){

    }

    public EnumWorkType getWorkType(){
        return WorkType;
    }

    public String getLocation(){
        return location;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}

