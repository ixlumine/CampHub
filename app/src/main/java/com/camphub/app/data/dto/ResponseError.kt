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

// Carries per-field validation messages so forms can mark each field (spec 6.2, rule 6)
class ApiException(message: String, val fieldErrors: Map<String, String>) : Exception(message)

fun ResponseError?.toApiException(fallback: String): ApiException =
    ApiException(
        message = toUserMessage(fallback),
        fieldErrors = this?.details?.associate { it.field to it.message } ?: emptyMap()
    )