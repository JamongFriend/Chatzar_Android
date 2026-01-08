package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.LoginRequest
import com.example.chatzar_android.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
