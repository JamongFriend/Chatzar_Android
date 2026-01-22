package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.AuthApi
import com.example.chatzar_android.data.remote.dto.LoginRequest
import com.example.chatzar_android.data.remote.dto.LoginResponse
import com.example.chatzar_android.data.remote.dto.SignupRequest
import com.example.chatzar_android.data.remote.dto.SignupResponse
import retrofit2.Response

class AuthRepository (
    private val authApi: AuthApi
) {
    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return authApi.login(request)
    }
    suspend fun signup(request: SignupRequest): Response<SignupResponse> {
        return authApi.signup(request)
    }
}
