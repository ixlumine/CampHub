package com.camphub.app.data.container

import com.camphub.app.data.interceptor.AuthInterceptor
import com.camphub.app.data.repository.AuthServerRepositories
import com.camphub.app.data.repository.BootcampServerRepositories
import com.camphub.app.data.repository.ForumServerRepositories
import com.camphub.app.data.service.AuthServerService
import com.camphub.app.data.service.BootcampServerService
import com.camphub.app.data.service.ForumServerService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CampHubServerContainer {
    companion object {
        // Emulator address for the laptop; for a phone via USB use http://127.0.0.1:8080/ after adb reverse
        val BASE_URL = "http://10.0.2.2:8080/"
        var ACCESS_TOKEN = ""
        var CURRENT_USER_ID: Long = -1
        var CURRENT_ROLE = ""
        var CURRENT_NAME = ""
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(ACCESS_TOKEN))
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    // Add each feature's service and repository below

    // Auth
    private val authService: AuthServerService by lazy {
        retrofit.create(AuthServerService::class.java)
    }

    val authServerRepository: AuthServerRepositories by lazy {
        AuthServerRepositories(authService)
    }

    // Bootcamp and program
    private val bootcampService: BootcampServerService by lazy {
        retrofit.create(BootcampServerService::class.java)
    }

    val bootcampServerRepository: BootcampServerRepositories by lazy {
        BootcampServerRepositories(bootcampService)
    }

    // Forum
    private val forumService: ForumServerService by lazy {
        retrofit.create(ForumServerService::class.java)
    }

    val forumServerRepository: ForumServerRepositories by lazy {
        ForumServerRepositories(forumService)
    }
}