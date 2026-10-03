package com.camphub.app.data.service

import com.camphub.app.data.dto.RequestForumComment
import com.camphub.app.data.dto.RequestForumPost
import com.camphub.app.data.dto.ResponseForumComment
import com.camphub.app.data.dto.ResponseForumPost
import com.camphub.app.data.dto.ResponseForumPostDetail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ForumServerService {
    @GET("api/forum/posts")
    suspend fun getPosts(): Response<List<ResponseForumPost>>

    @GET("api/forum/posts/{id}")
    suspend fun getPostDetail(@Path("id") id: Long): Response<ResponseForumPostDetail>

    @POST("api/forum/posts")
    suspend fun createPost(@Body request: RequestForumPost): Response<ResponseForumPost>

    @PUT("api/forum/posts/{id}")
    suspend fun updatePost(@Path("id") id: Long, @Body request: RequestForumPost): Response<ResponseForumPost>

    @DELETE("api/forum/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    @POST("api/forum/posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Long,
        @Body request: RequestForumComment
    ): Response<ResponseForumComment>

    @DELETE("api/forum/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Long): Response<Unit>
}
