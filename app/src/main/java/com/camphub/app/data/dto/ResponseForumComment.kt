package com.camphub.app.data.dto

import com.camphub.app.ui.model.ForumComment

data class ResponseForumComment(
    val id: Long? = null,
    val postId: Long? = null,
    val userId: Long? = null,
    val userName: String? = null,
    val content: String
)

fun ResponseForumComment.toForumComment(): ForumComment = ForumComment(
    id = id ?: 0,
    postId = postId ?: 0,
    userId = userId ?: -1,
    userName = userName ?: "",
    content = content
)
