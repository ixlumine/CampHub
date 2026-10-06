package com.camphub.app.data.repository

import com.camphub.app.data.dto.RequestForumComment
import com.camphub.app.data.dto.RequestForumThread
import com.camphub.app.data.dto.ResponseError
import com.camphub.app.data.dto.toApiException
import com.camphub.app.data.dto.toForumComment
import com.camphub.app.data.dto.toForumThread
import com.camphub.app.data.service.ForumServerService
import com.camphub.app.ui.model.ForumComment
import com.camphub.app.ui.model.ForumThread
import com.google.gson.Gson

class ForumServerRepositories(private val service: ForumServerService) {

    suspend fun getAllThreads(): List<ForumThread> {
        val response = service.getThreads()
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat forum (${response.code()})")
        }
        return response.body()!!.map { it.toForumThread() }
    }

    suspend fun getThread(id: Long): ForumThread {
        val response = service.getThread(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat detail diskusi (${response.code()})")
        }
        return response.body()!!.toForumThread()
    }

    suspend fun getComments(threadId: Long): List<ForumComment> {
        val response = service.getComments(threadId)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat komentar (${response.code()})")
        }
        return response.body()!!.map { it.toForumComment() }
    }

    suspend fun createThread(title: String, content: String): ForumThread {
        val request = RequestForumThread(title = title, content = content)
        val response = service.createThread(request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal membuat diskusi (${response.code()})")
        }
        return response.body()!!.toForumThread()
    }

    suspend fun updateThread(id: Long, title: String, content: String): ForumThread {
        val request = RequestForumThread(title = title, content = content)
        val response = service.updateThread(id, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal mengubah diskusi (${response.code()})")
        }
        return response.body()!!.toForumThread()
    }

    suspend fun deleteThread(id: Long) {
        val response = service.deleteThread(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menghapus diskusi (${response.code()})")
        }
    }

    suspend fun addComment(threadId: Long, content: String): ForumComment {
        val request = RequestForumComment(content = content)
        val response = service.addComment(threadId, request)
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
