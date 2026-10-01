package com.camphub.app.data.dto

import com.camphub.app.ui.model.Bootcamp

data class ResponseBootcamp(
    val description: String,
    val id: Long,
    val location: String,
    val name: String,
    val ownerId: Long,
    val ownerName: String,
    val website: String?
)

fun ResponseBootcamp.toBootcamp(): Bootcamp =
    Bootcamp(
        id = id,
        name = name,
        description = description,
        location = location,
        website = website,
        ownerId = ownerId,
        ownerName = ownerName
    )