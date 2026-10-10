package com.camphub.app.data.repository

import com.camphub.app.data.dto.RequestReview
import com.camphub.app.data.dto.ResponseError
import com.camphub.app.data.dto.toApiException
import com.camphub.app.data.dto.toRanking
import com.camphub.app.data.dto.toReview
import com.camphub.app.data.dto.toReviewSummary
import com.camphub.app.data.service.ReviewServerService
import com.camphub.app.ui.model.Ranking
import com.camphub.app.ui.model.Review
import com.camphub.app.ui.model.ReviewSummary
import com.google.gson.Gson

class ReviewServerRepositories(private val service: ReviewServerService) {

    suspend fun getReviews(bootcampId: Long): List<Review> {
        val response = service.getReviews(bootcampId)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat ulasan (${response.code()})")
        }
        return response.body()!!.map { it.toReview() }
    }

    suspend fun createReview(bootcampId: Long, rating: Int, content: String, careerStatus: String): Review {
        val request = RequestReview(rating = rating, content = content, careerStatus = careerStatus)
        val response = service.createReview(bootcampId, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menyimpan ulasan (${response.code()})")
        }
        return response.body()!!.toReview()
    }

    suspend fun updateReview(id: Long, rating: Int, content: String, careerStatus: String): Review {
        val request = RequestReview(rating = rating, content = content, careerStatus = careerStatus)
        val response = service.updateReview(id, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menyimpan ulasan (${response.code()})")
        }
        return response.body()!!.toReview()
    }

    suspend fun deleteReview(id: Long) {
        val response = service.deleteReview(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menghapus ulasan (${response.code()})")
        }
    }

    suspend fun getReviewSummary(bootcampId: Long): ReviewSummary {
        val response = service.getReviewSummary(bootcampId)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat ringkasan ulasan (${response.code()})")
        }
        return response.body()!!.toReviewSummary()
    }

    suspend fun getRankings(): List<Ranking> {
        val response = service.getRankings()
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat peringkat (${response.code()})")
        }
        return response.body()!!.map { it.toRanking() }
    }
}
