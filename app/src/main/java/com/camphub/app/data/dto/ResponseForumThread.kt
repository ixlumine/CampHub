package com.camphub.app.data.dto

import com.camphub.app.ui.model.ForumThread

data class ResponseForumThread(
    val id: Long? = null,
    val authorId: Long? = null,
    val authorName: String? = null,
    val title: String,
    val content: String,
    val commentCount: Int = 0,
    val createdAt: String? = null
)

fun ResponseForumThread.toForumThread(): ForumThread = ForumThread(
    id = id ?: 0,
    authorId = authorId ?: -1,
    authorName = authorName ?: "",
    title = title,
    content = content,
    commentCount = commentCount,
    createdAt = createdAt ?: ""
)
