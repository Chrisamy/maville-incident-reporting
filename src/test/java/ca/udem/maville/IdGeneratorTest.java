package ca.udem.maville;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    @Test
    void testGenerateID_notNull() {
        String id = IdGenerator.generateID();
        assertNotNull(id, "ID should not be null");
    }

    @Test
    void testGenerateID_notEmpty() {
        String id = IdGenerator.generateID();
        assertFalse(id.isEmpty(), "ID should not be empty");
    }

    @Test
    void testGenerateID_validUUIDFormat() {
        String id = IdGenerator.generateID();
        assertDoesNotThrow(() -> {
            UUID.fromString(id);
        }, "ID should be valid UUID format");
    }

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

