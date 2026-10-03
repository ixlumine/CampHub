package com.camphub.app.data.dto

import com.camphub.app.ui.model.ForumPost

data class ResponseForumPost(
    val id: Long? = null,
    val title: String,
    val content: String,
    val userId: Long? = null,
    val userName: String? = null,
    val commentsCount: Int = 0
)

fun ResponseForumPost.toForumPost(): ForumPost = ForumPost(
    id = id ?: 0,
    title = title,
    content = content,
    userId = userId ?: -1,
    userName = userName ?: "",
    commentsCount = commentsCount
)
