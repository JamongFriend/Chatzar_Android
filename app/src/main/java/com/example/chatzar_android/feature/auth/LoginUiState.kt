package com.example.chatzar_android.feature.auth

import com.example.chatzar_android.data.remote.dto.LoginResponse

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
