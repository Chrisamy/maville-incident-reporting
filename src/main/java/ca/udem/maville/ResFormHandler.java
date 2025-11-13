package ca.udem.maville;

public class ResFormHandler {
    FormResident form;
    String id;

    String priority;

    public void setId(String id){
        this.id = id;
    }

    public void setPriority(String priority){
        this.priority = priority;
    }

    public Boolean isFormValid(){
        return true;
    }

    public Boolean isFormDuplicate(){
        return false;
    }
}
