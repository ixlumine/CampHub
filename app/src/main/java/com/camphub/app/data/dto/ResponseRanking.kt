package com.camphub.app.data.dto

import com.camphub.app.ui.model.Ranking

data class ResponseRanking(
    val rank: Int,
    val bootcampId: Long,
    val bootcampName: String,
    val location: String,
    val averageRating: Double,
    val reviewCount: Long
)

fun ResponseRanking.toRanking(): Ranking =
    Ranking(
        rank = rank,
        bootcampId = bootcampId,
        bootcampName = bootcampName,
        location = location,
        averageRating = averageRating,
        reviewCount = reviewCount
    )
