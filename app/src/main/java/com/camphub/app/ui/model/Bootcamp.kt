package com.camphub.app.ui.model

data class Bootcamp(
    val id: Long,
    val name: String,
    val description: String,
    val location: String,
    val website: String?,
    // Provider who owns this bootcamp
    val ownerId: Long,
    val ownerName: String
)