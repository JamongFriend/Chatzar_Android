package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.AuthApi
import com.example.chatzar_android.data.remote.dto.LoginRequest
import com.example.chatzar_android.data.remote.dto.LoginResponse

class AuthRepository (
    private val authApi: AuthApi
) {
    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return authApi.login(request)
    }
    }
}
