package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.LoginRequest
import com.example.chatzar_android.data.remote.dto.LoginResponse
import com.example.chatzar_android.data.remote.dto.SignupRequest
import com.example.chatzar_android.data.remote.dto.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>
}
