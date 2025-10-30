package org.example.app.util

// PUBLIC_INTERFACE
data class LogEntry(
    /** ISO-like time string for display */
    val time: String,
    /** Level like INFO/WARN/ERROR */
    val level: String,
    /** Message text */
    val message: String
)
