package com.camphub.app.data.dto

// Used for create and update
data class RequestBootcamp(
    val description: String,
    val location: String,
    val name: String,
    val website: String?
)