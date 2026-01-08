package com.example.chatzar_android.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val repo: AuthRepository
) :ViewModel() {
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
                val res = repo.login(email, password)
                _state.value = LoginUiState.Success(res)
            } catch (e: HttpException) {
                _state.value = LoginUiState.Error("로그인 실패(HTTP ${e.code()})")
            } catch (e: IOException) {
                _state.value = LoginUiState.Error("네트워크 오류")
            } catch (e: Exception) {
                _state.value = LoginUiState.Error("알 수 없는 오류")
            }
        }
    }
}
