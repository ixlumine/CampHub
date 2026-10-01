package com.camphub.app.data.dto

import com.camphub.app.ui.model.Program

data class ResponseProgram(
    val bootcampId: Long,
    val bootcampName: String,
    val category: String,
    val durationWeeks: Int,
    val id: Long,
    val name: String,
    val price: Long,
    val registrationOpen: Boolean,
    val syllabus: String
)

fun ResponseProgram.toProgram(): Program =
    Program(
        id = id,
        bootcampId = bootcampId,
        bootcampName = bootcampName,
        name = name,
        category = category,
        price = price,
        durationWeeks = durationWeeks,
        syllabus = syllabus,
        registrationOpen = registrationOpen
    )