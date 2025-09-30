package ca.udem.maville;

public class Request {

    String address;
    String type;
    String enTraitement;
    String numeroEntreprise;

    public Request(String Address, String Type) {
        address = Address;
        type = Type;
        enTraitement = "Non";
    }

    public String getNe(){
        if (numeroEntreprise == null) {
            return "pas d'entreprise";
        } else {
            return numeroEntreprise;
        }
    }

    public void setNe(String numeroEntreprise) {
        this.numeroEntreprise = numeroEntreprise;
    }

}
