package com.example.chatzar_android.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.SignupRequest
import com.example.chatzar_android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val state: StateFlow<SignupUiState> = _state

    fun signup(request: SignupRequest) {
        viewModelScope.launch {
            _state.value = SignupUiState.Loading
            try {
                val response = repository.signup(request)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = SignupUiState.Success(response.body()!!)
                } else {
                    _state.value = SignupUiState.Error("회원가입 실패: ${response.message()}")
                }
            } catch (e: Exception) {
                _state.value = SignupUiState.Error(e.message ?: "알 수 없는 오류 발생")
            }
        }
    }
}
