package ca.udem.maville;

public class Resident extends User{

    public Resident() {
        super();
    }

    public void submitForm(FormResident form){
        ResFormHandler resHandler = new ResFormHandler(form);
    }

}
