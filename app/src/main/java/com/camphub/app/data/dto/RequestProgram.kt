package com.camphub.app.data.dto

// Used for create and update
data class RequestProgram(
    val category: String,
    val durationWeeks: Int,
    val name: String,
    val price: Long,
    val registrationOpen: Boolean,
    val syllabus: String
)