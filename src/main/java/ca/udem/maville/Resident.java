package ca.udem.maville;

public class Resident extends User{

    public Resident(String username, String password) {
        super(username, password);
    }

    private void submitForm(String location, String username, String descritption){
        FormResident form = new FormResident(location, username, descritption);
        ProblemRepository.addForm(form);
    }

}
