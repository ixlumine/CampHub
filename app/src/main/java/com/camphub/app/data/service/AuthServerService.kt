package com.camphub.app.data.service

import com.camphub.app.data.dto.RequestLogin
import com.camphub.app.data.dto.RequestRegister
import com.camphub.app.data.dto.ResponseAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthServerService {
    @POST("api/auth/login")
    suspend fun login(@Body request: RequestLogin): Response<ResponseAuth>

    @POST("api/auth/register")
    suspend fun register(@Body request: RequestRegister): Response<ResponseAuth>
}