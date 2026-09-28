package com.camphub.app.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response

// Adds the JWT to every request (lecturer demo pattern)
class AuthInterceptor(private val bearerToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer $bearerToken")
            .build()
        return chain.proceed(request)
    }
}