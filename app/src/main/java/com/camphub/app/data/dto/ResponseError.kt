package com.camphub.app.data.dto

// Error body from the backend (spec 9.1)
data class ResponseError(
    val details: List<Detail>?,
    val error: String,
    val message: String,
    val path: String,
    val status: Int,
    val timestamp: String
)

data class Detail(
    val field: String,
    val message: String
)

// Validation errors -> field messages; other errors -> server message
fun ResponseError?.toUserMessage(fallback: String): String =
    this?.details?.takeIf { it.isNotEmpty() }?.joinToString("\n") { it.message }
        ?: this?.message
        ?: fallback