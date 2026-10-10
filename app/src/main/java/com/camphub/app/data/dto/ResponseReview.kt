package com.camphub.app.data.dto

import com.camphub.app.ui.model.Review

data class ResponseReview(
    val id: Long,
    val bootcampId: Long,
    val bootcampName: String,
    val authorId: Long,
    val authorName: String,
    val rating: Int,
    val content: String,
    val careerStatus: String,
    val createdAt: String
)

fun ResponseReview.toReview(): Review =
    Review(
        id = id,
        bootcampId = bootcampId,
        bootcampName = bootcampName,
        authorId = authorId,
        authorName = authorName,
        rating = rating,
        content = content,
        careerStatus = careerStatus,
        createdAt = createdAt
    )
