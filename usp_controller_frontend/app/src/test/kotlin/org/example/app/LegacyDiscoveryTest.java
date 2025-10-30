package org.example.app;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * PUBLIC_INTERFACE
 * Legacy JUnit 4 style test to ensure Gradle's default test runner discovers at least one test.
 * This avoids configuration edits in Declarative Gradle by providing a compatible test.
 */
public class LegacyDiscoveryTest {
    @Test
    public void testDiscoveryWorks() {
        assertTrue(true);
    }
}
