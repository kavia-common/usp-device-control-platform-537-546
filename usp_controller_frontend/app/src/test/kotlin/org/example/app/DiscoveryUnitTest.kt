package org.example.app

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * PUBLIC_INTERFACE
 * Simple JUnit Jupiter unit test to ensure Gradle discovers tests with the configured JUnit 5 dependency.
 */
class DiscoveryUnitTest {
    @Test
    fun alwaysTrue() {
        assertTrue(true)
    }
}
