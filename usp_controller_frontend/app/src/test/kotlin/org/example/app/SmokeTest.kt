package org.example.app

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * PUBLIC_INTERFACE
 * Minimal smoke test to ensure the Gradle test task discovers at least one test and does not fail the build.
 */
class SmokeTest {
    @Test
    fun testAlwaysPasses() {
        assertTrue(true)
    }
}
