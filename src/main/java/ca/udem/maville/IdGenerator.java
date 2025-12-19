package ca.udem.maville;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() { }

    //Méthode utilisée pour générer les ID des formulaires et des demandes

    public static String generateID(){
        return UUID.randomUUID().toString();
    }
}
