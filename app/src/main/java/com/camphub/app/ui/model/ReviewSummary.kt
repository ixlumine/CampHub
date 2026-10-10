package com.camphub.app.ui.model

data class ReviewSummary(
    val bootcampId: Long,
    val averageRating: Double,
    val reviewCount: Long,
    val employedCount: Long,
    val seekingJobCount: Long
)
