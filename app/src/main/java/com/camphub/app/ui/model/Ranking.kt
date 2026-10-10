package com.camphub.app.ui.model

data class Ranking(
    val rank: Int,
    val bootcampId: Long,
    val bootcampName: String,
    val location: String,
    val averageRating: Double,
    val reviewCount: Long
)
