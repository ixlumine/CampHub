package com.camphub.app.data.service

import com.camphub.app.data.dto.RequestReview
import com.camphub.app.data.dto.ResponseRanking
import com.camphub.app.data.dto.ResponseReview
import com.camphub.app.data.dto.ResponseReviewSummary
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewServerService {

    @GET("api/bootcamps/{id}/reviews")
    suspend fun getReviews(@Path("id") bootcampId: Long): Response<List<ResponseReview>>

    @POST("api/bootcamps/{id}/reviews")
    suspend fun createReview(
        @Path("id") bootcampId: Long,
        @Body request: RequestReview
    ): Response<ResponseReview>

    @PUT("api/reviews/{id}")
    suspend fun updateReview(
        @Path("id") id: Long,
        @Body request: RequestReview
    ): Response<ResponseReview>

    @DELETE("api/reviews/{id}")
    suspend fun deleteReview(@Path("id") id: Long): Response<Unit>

    @GET("api/bootcamps/{id}/review-summary")
    suspend fun getReviewSummary(@Path("id") bootcampId: Long): Response<ResponseReviewSummary>

    @GET("api/rankings")
    suspend fun getRankings(): Response<List<ResponseRanking>>
}
