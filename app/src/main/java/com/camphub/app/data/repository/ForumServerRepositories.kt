package com.camphub.app.data.repository

import com.camphub.app.data.dto.RequestForumComment
import com.camphub.app.data.dto.RequestForumPost
import com.camphub.app.data.dto.ResponseError
import com.camphub.app.data.dto.toApiException
import com.camphub.app.data.dto.toForumComment
import com.camphub.app.data.dto.toForumPost
import com.camphub.app.data.service.ForumServerService
import com.camphub.app.ui.model.ForumComment
import com.camphub.app.ui.model.ForumPost
import com.google.gson.Gson

class ForumServerRepositories(private val service: ForumServerService) {

    suspend fun getAllPosts(): List<ForumPost> {
        val response = service.getPosts()
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat forum (${response.code()})")
        }
        return response.body()!!.map { it.toForumPost() }
    }

    suspend fun getPostDetail(id: Long): Pair<ForumPost, List<ForumComment>> {
        val response = service.getPostDetail(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat detail diskusi (${response.code()})")
        }
        val detail = response.body()!!
        return Pair(detail.post.toForumPost(), detail.comments.map { it.toForumComment() })
    }

    suspend fun createPost(title: String, content: String): ForumPost {
        val request = RequestForumPost(title = title, content = content)
        val response = service.createPost(request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal membuat diskusi (${response.code()})")
        }
        return response.body()!!.toForumPost()
    }

    suspend fun updatePost(id: Long, title: String, content: String): ForumPost {
        val request = RequestForumPost(title = title, content = content)
        val response = service.updatePost(id, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal mengubah diskusi (${response.code()})")
        }
        return response.body()!!.toForumPost()
    }

    suspend fun deletePost(id: Long) {
        val response = service.deletePost(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menghapus diskusi (${response.code()})")
        }
    }

    suspend fun addComment(postId: Long, content: String): ForumComment {
        val request = RequestForumComment(content = content)
        val response = service.addComment(postId, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal mengirim komentar (${response.code()})")
        }
        return response.body()!!.toForumComment()
    }

    suspend fun deleteComment(commentId: Long) {
        val response = service.deleteComment(commentId)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menghapus komentar (${response.code()})")
        }
    }
}
