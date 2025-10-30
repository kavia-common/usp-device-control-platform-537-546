package org.example.app.util

// PUBLIC_INTERFACE
sealed class Result<out T> {
    /** Represents a successful result with optional data. */
    data class Success<T>(val data: T? = null): Result<T>()
    /** Represents an error with message and optional throwable. */
    data class Error(val message: String, val throwable: Throwable? = null): Result<Nothing>()
}
