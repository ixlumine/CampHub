package com.camphub.app.data.dto

data class ResponseForumPostDetail(
    val post: ResponseForumPost,
    val comments: List<ResponseForumComment>
)
