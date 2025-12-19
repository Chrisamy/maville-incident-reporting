package ca.udem.maville;

public class Resident extends User{

    public Resident() {
        super();
    }

    //Méthode pour soumettre un formulaire

    public void submitForm(ProblemForm form){
        ProblemRepository.addForm(form);
        Server.sendMessageToUI("Probleme Soumis!");
    }

}
