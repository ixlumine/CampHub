package com.camphub.app.data.dto

data class ResponseAuth(
    val name: String,
    val role: String,
    val token: String,
    val tokenType: String,
    val userId: Long
)