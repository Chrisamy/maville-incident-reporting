package ca.udem.maville;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    //Test qui vérifie qu'un ID généré n'est pas null

    @Test
    void testGenerateID_notNull() {
        String id = IdGenerator.generateID();
        assertNotNull(id, "ID should not be null");
    }

    //Test qui vérifie qu'un ID généré n'est pas vide

    @Test
    void testGenerateID_notEmpty() {
        String id = IdGenerator.generateID();
        assertFalse(id.isEmpty(), "ID should not be empty");
    }

    //Test qui vérifie qu'un ID généré correspond au format valide

    @Test
    void testGenerateID_validUUIDFormat() {
        String id = IdGenerator.generateID();
        assertDoesNotThrow(() -> {
            UUID.fromString(id);
        }, "ID should be valid UUID format");
    }

    //Test qui vérifie que 2 ID générés n'ont pas la même valeur

    @Test
    void generateIdProducesNonEmptyUniqueValues() {
        String a = IdGenerator.generateID();
        String b = IdGenerator.generateID();
        assertNotNull(a);
        assertNotNull(b);
        assertFalse(a.isEmpty());
        assertFalse(b.isEmpty());
        assertNotEquals(a, b, "two generated IDs should not be equal");
    }
}

