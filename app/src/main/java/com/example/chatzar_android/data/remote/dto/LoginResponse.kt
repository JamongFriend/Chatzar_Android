package com.example.chatzar_android.data.remote.dto

data class LoginResponse(
    val memberId: Long,
    val accessToken: String? = null
)
