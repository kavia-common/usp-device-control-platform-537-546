package org.example.app;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * PUBLIC_INTERFACE
 * Minimal test under androidTest source set to ensure Gradle discovers tests.
 * This test does not run any Android-dependent code and simply validates discovery.
 */
public class DiscoveryInstrumentedTest {
    @Test
    public void discovery_runs() {
        assertTrue(true);
    }
}
