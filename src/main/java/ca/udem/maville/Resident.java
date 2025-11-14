package ca.udem.maville;

public class Resident extends User{

    public Resident() {
        super();
    }

    public void submitForm(ProblemForm form){
        Server.problemList.addForm(form);
        Server.sendMessageToUI("Probleme Soumis!");
    }

}
