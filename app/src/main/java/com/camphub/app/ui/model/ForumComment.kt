package com.camphub.app.ui.model

data class ForumComment(
    val id: Long,
    val postId: Long,
    val userId: Long,
    val userName: String,
    val content: String
)
