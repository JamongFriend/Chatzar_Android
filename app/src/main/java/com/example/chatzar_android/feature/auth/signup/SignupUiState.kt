package com.example.chatzar_android.feature.auth.signup

import com.example.chatzar_android.data.remote.dto.SignupResponse

sealed class SignupUiState {
    object Idle : SignupUiState()
    object Loading : SignupUiState()
    data class Success(val data: SignupResponse) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}
