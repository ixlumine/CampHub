package com.camphub.app.data.service

import com.camphub.app.data.dto.RequestBootcamp
import com.camphub.app.data.dto.RequestProgram
import com.camphub.app.data.dto.ResponseBootcamp
import com.camphub.app.data.dto.ResponseProgram
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BootcampServerService {
    // Bootcamp
    @GET("api/bootcamps")
    suspend fun getBootcamps(): Response<List<ResponseBootcamp>>

    @GET("api/bootcamps/{id}")
    suspend fun getBootcamp(@Path("id") id: Long): Response<ResponseBootcamp>

    @POST("api/bootcamps")
    suspend fun createBootcamp(@Body request: RequestBootcamp): Response<ResponseBootcamp>

    @PUT("api/bootcamps/{id}")
    suspend fun updateBootcamp(@Path("id") id: Long, @Body request: RequestBootcamp): Response<ResponseBootcamp>

    @DELETE("api/bootcamps/{id}")
    suspend fun deleteBootcamp(@Path("id") id: Long): Response<Unit>

    // Program
    @GET("api/bootcamps/{id}/programs")
    suspend fun getPrograms(@Path("id") bootcampId: Long): Response<List<ResponseProgram>>

    @GET("api/programs/{id}")
    suspend fun getProgram(@Path("id") id: Long): Response<ResponseProgram>

    @POST("api/bootcamps/{id}/programs")
    suspend fun createProgram(@Path("id") bootcampId: Long, @Body request: RequestProgram): Response<ResponseProgram>

    @PUT("api/programs/{id}")
    suspend fun updateProgram(@Path("id") id: Long, @Body request: RequestProgram): Response<ResponseProgram>

    @DELETE("api/programs/{id}")
    suspend fun deleteProgram(@Path("id") id: Long): Response<Unit>
}