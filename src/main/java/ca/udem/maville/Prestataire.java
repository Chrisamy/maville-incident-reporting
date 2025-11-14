package ca.udem.maville;

public class Prestataire extends User {

    int CompanyNumber;

    public Prestataire(String username, String password, int CompanyNumber) {
        super(username, password);
        this.CompanyNumber = CompanyNumber;
    }

    public int getCompanyNumber() {
        return CompanyNumber;
    }

    public void setCompanyNumber(int CompanyNumber) {
        this.CompanyNumber = CompanyNumber;
    }
}
