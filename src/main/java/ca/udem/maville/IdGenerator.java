package ca.udem.maville;

import java.util.HashSet;
import java.util.Set;
import java.security.SecureRandom;

public class IdGenerator {

    private static final String charHexa = "0123456789abcdef";
    private static final SecureRandom random = new SecureRandom();
    private static Set<String> setID = new HashSet<>();

    public static String generateID(){
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++) {
            int number = random.nextInt(16);
            sb.append(charHexa.charAt(number));
        }

        String id = sb.toString();

        if (setID.contains(id)) {
            generateID();
        }
        else {
            setID.add(id);
            return id;
        }
        return null;
    }

}

