package com.camphub.app.data.dto

// Used for create and update
data class RequestReview(
    val rating: Int,
    val content: String,
    val careerStatus: String
)
