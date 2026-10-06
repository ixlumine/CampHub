package com.camphub.app.ui.model

data class ForumThread(
    val id: Long,
    val authorId: Long,
    val authorName: String,
    val title: String,
    val content: String,
    val commentCount: Int,
    val createdAt: String
)
