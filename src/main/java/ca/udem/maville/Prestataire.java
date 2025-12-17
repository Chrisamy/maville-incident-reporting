package ca.udem.maville;

public class Prestataire extends User {

    int CompanyNumber;

    public int getCompanyNumber() {
        return CompanyNumber;
    }

    public void setCompanyNumber(int CompanyNumber) {
        this.CompanyNumber = CompanyNumber;
    }

    public void submitDemand(DemandForm demandForm) {
        DemandRepository.addDemand(demandForm);
        Server.sendMessageToUI("Demande Soumise!");
    }
}
