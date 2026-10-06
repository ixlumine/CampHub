package com.camphub.app.data.service

import com.camphub.app.data.dto.RequestForumComment
import com.camphub.app.data.dto.RequestForumThread
import com.camphub.app.data.dto.ResponseForumComment
import com.camphub.app.data.dto.ResponseForumThread
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ForumServerService {
    @GET("api/threads")
    suspend fun getThreads(): Response<List<ResponseForumThread>>

    @GET("api/threads/{id}")
    suspend fun getThread(@Path("id") id: Long): Response<ResponseForumThread>

    @POST("api/threads")
    suspend fun createThread(@Body request: RequestForumThread): Response<ResponseForumThread>

    @PUT("api/threads/{id}")
    suspend fun updateThread(@Path("id") id: Long, @Body request: RequestForumThread): Response<ResponseForumThread>

    @DELETE("api/threads/{id}")
    suspend fun deleteThread(@Path("id") id: Long): Response<Unit>

    @GET("api/threads/{id}/comments")
    suspend fun getComments(@Path("id") threadId: Long): Response<List<ResponseForumComment>>

    @POST("api/threads/{threadId}/comments")
    suspend fun addComment(
        @Path("threadId") threadId: Long,
        @Body request: RequestForumComment
    ): Response<ResponseForumComment>

    @DELETE("api/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Long): Response<Unit>
}
