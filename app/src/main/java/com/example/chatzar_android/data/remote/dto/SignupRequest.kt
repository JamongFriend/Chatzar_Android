package com.example.chatzar_android.data.remote.dto

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val nickname: String,
    val age: Long
)
