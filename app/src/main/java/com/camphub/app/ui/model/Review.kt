package com.camphub.app.ui.model

data class Review(
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
