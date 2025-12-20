package ca.udem.maville;

public class Prestataire extends User {

    int CompanyNumber;

    public int getCompanyNumber() {
        return CompanyNumber;
    }

    public void setCompanyNumber(int CompanyNumber) {
        this.CompanyNumber = CompanyNumber;
    }

    //Méthode pour envoyer une demande de projet

    public void submitDemand(PrestataireForm prestataireForm) {
        CandidatRepository.addDemand(prestataireForm);
        Server.sendMessageToUI("Demande Soumise!");
    }

}
