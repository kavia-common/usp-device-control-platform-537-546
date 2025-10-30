package org.example.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class MessageUtilsTest {
    @Test
    fun testGetMessage() {
        // MessageUtils.message() returns "Hello     World!" (5 spaces)
        assertEquals("Hello     World!", MessageUtils.message())
    }
}
