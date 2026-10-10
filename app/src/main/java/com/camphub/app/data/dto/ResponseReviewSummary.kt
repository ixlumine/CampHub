package com.camphub.app.data.dto

import com.camphub.app.ui.model.ReviewSummary

data class ResponseReviewSummary(
    val bootcampId: Long,
    val averageRating: Double,
    val reviewCount: Long,
    val employedCount: Long,
    val seekingJobCount: Long
)

fun ResponseReviewSummary.toReviewSummary(): ReviewSummary =
    ReviewSummary(
        bootcampId = bootcampId,
        averageRating = averageRating,
        reviewCount = reviewCount,
        employedCount = employedCount,
        seekingJobCount = seekingJobCount
    )
