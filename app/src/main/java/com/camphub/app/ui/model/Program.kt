package com.camphub.app.ui.model

data class Program(
    val id: Long,
    val bootcampId: Long,
    val bootcampName: String,
    val name: String,
    val category: String,
    // In rupiah
    val price: Long,
    val durationWeeks: Int,
    val syllabus: String,
    val registrationOpen: Boolean
)