package com.example.chatzar_android.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.LoginRequest
import com.example.chatzar_android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(private val repository: AuthRepository) :ViewModel() {
    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state

    fun login(email: String, password: String) {
        if(email.isBlank() || password.isBlank()){
            _state.value = LoginUiState.Error("이메일/비밀번호를 입력하세요.")
            return
        }
        viewModelScope.launch {
            _state.value = LoginUiState.Loading
            try {
                val request = LoginRequest(email, password)
                val response = repository.login(request)

                if (response.isSuccessful && response.body() != null) {
                    _state.value = LoginUiState.Success(response.body()!!)
                } else {
                    _state.value = LoginUiState.Error("로그인 실패")
                }
            } catch (e: Exception) {
                _state.value = LoginUiState.Error(e.message ?: "에러 발생")
            }
        }
    }
}
