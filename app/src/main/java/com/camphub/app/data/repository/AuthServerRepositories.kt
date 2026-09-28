package com.camphub.app.data.repository

import com.camphub.app.data.dto.RequestLogin
import com.camphub.app.data.dto.RequestRegister
import com.camphub.app.data.dto.ResponseAuth
import com.camphub.app.data.dto.ResponseError
import com.camphub.app.data.dto.toUserMessage
import com.camphub.app.data.service.AuthServerService
import com.google.gson.Gson

class AuthServerRepositories(private val service: AuthServerService) {

    suspend fun login(email: String, password: String): ResponseAuth {
        val response = service.login(RequestLogin(email = email, password = password))
        if (!response.isSuccessful) {
            // Server message from ResponseError (6.2, rule 6)
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw Exception(error.toUserMessage("Login gagal (${response.code()})"))
        }
        return response.body()!!
    }

    suspend fun register(name: String, email: String, password: String): ResponseAuth {
        val response = service.register(RequestRegister(email = email, name = name, password = password))
        if (!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.charStream(), ResponseError::class.java)
            throw Exception(error.toUserMessage("Registrasi gagal (${response.code()})"))
        }
        return response.body()!!
    }
}