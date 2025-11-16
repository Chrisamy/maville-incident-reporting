package ca.udem.maville;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() { }

    public static String generateID(){
        return UUID.randomUUID().toString();
    }
}
