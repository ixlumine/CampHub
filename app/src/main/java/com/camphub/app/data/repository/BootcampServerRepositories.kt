package com.camphub.app.data.repository

import com.camphub.app.data.dto.RequestBootcamp
import com.camphub.app.data.dto.RequestProgram
import com.camphub.app.data.dto.ResponseError
import com.camphub.app.data.dto.toApiException
import com.camphub.app.data.dto.toBootcamp
import com.camphub.app.data.dto.toProgram
import com.camphub.app.data.service.BootcampServerService
import com.camphub.app.ui.model.Bootcamp
import com.camphub.app.ui.model.Program
import com.google.gson.Gson

class BootcampServerRepositories(private val service: BootcampServerService) {

    // Bootcamp

    suspend fun getAllBootcamps(): List<Bootcamp> {
        val response = service.getBootcamps()
        if (!response.isSuccessful) {
            // Read the error message sent by the server
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat bootcamp (${response.code()})")
        }
        return response.body()!!.map { it.toBootcamp() }
    }

    suspend fun getBootcamp(id: Long): Bootcamp {
        val response = service.getBootcamp(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat bootcamp (${response.code()})")
        }
        return response.body()!!.toBootcamp()
    }

    suspend fun createBootcamp(name: String, description: String, location: String, website: String?): Bootcamp {
        val request = RequestBootcamp(description = description, location = location, name = name, website = website)
        val response = service.createBootcamp(request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menyimpan bootcamp (${response.code()})")
        }
        return response.body()!!.toBootcamp()
    }

    suspend fun updateBootcamp(id: Long, name: String, description: String, location: String, website: String?): Bootcamp {
        val request = RequestBootcamp(description = description, location = location, name = name, website = website)
        val response = service.updateBootcamp(id, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menyimpan bootcamp (${response.code()})")
        }
        return response.body()!!.toBootcamp()
    }

    suspend fun deleteBootcamp(id: Long) {
        val response = service.deleteBootcamp(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menghapus bootcamp (${response.code()})")
        }
    }

    // Program

    suspend fun getPrograms(bootcampId: Long): List<Program> {
        val response = service.getPrograms(bootcampId)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat program (${response.code()})")
        }
        return response.body()!!.map { it.toProgram() }
    }

    suspend fun getProgram(id: Long): Program {
        val response = service.getProgram(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal memuat program (${response.code()})")
        }
        return response.body()!!.toProgram()
    }

    suspend fun createProgram(
        bootcampId: Long,
        name: String,
        category: String,
        price: Long,
        durationWeeks: Int,
        syllabus: String,
        registrationOpen: Boolean
    ): Program {
        val request = RequestProgram(
            category = category,
            durationWeeks = durationWeeks,
            name = name,
            price = price,
            registrationOpen = registrationOpen,
            syllabus = syllabus
        )
        val response = service.createProgram(bootcampId, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menyimpan program (${response.code()})")
        }
        return response.body()!!.toProgram()
    }

    suspend fun updateProgram(
        id: Long,
        name: String,
        category: String,
        price: Long,
        durationWeeks: Int,
        syllabus: String,
        registrationOpen: Boolean
    ): Program {
        val request = RequestProgram(
            category = category,
            durationWeeks = durationWeeks,
            name = name,
            price = price,
            registrationOpen = registrationOpen,
            syllabus = syllabus
        )
        val response = service.updateProgram(id, request)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menyimpan program (${response.code()})")
        }
        return response.body()!!.toProgram()
    }

    suspend fun deleteProgram(id: Long) {
        val response = service.deleteProgram(id)
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw error.toApiException("Gagal menghapus program (${response.code()})")
        }
    }
}