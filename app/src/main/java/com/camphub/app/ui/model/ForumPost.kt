package com.camphub.app.ui.model

data class ForumPost(
    val id: Long,
    val title: String,
    val content: String,
    val userId: Long,
    val userName: String,
    val commentsCount: Int
)
