package io.siddhant.expense_tracker.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

data class ApplicationResponse(
    private val message: String,
    private val status: HttpStatus,
    private val data: Any? = null
) {
    private val timestamp = LocalDateTime.now()

    fun build(): ResponseEntity<Map<String, Any?>> {
        return ResponseEntity(mapOf("timestamp" to timestamp, "message" to message, "data" to data), status)
    }
}
