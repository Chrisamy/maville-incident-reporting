package ca.udem.maville;

public class Prestataire extends User {

    int CompanyNumber;

    public Prestataire(String username, String password) {
        super(username, password);
    }

    public int getCompanyNumber() {
        return CompanyNumber;
    }

    public void setCompanyNumber(int CompanyNumber) {
        this.CompanyNumber = CompanyNumber;
    }
}
