package ca.udem.maville;

public class Request {

    String address;
    String type;
    String en_traitement;

    public Request(String Address, String Type) {
        address = Address;
        type = Type;
        en_traitement = "Non";
    }

}
