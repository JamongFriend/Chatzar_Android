package com.example.chatzar_android.feature.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.MemberResponse
import com.example.chatzar_android.data.repository.MemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MyPageUiState {
    object Idle : MyPageUiState()
    object Loading : MyPageUiState()
    data class Success(val member: MemberResponse) : MyPageUiState()
    data class Error(val message: String) : MyPageUiState()
}

class MyPageViewModel(private val repository: MemberRepository) : ViewModel() {

    private val _state = MutableStateFlow<MyPageUiState>(MyPageUiState.Idle)
    val state: StateFlow<MyPageUiState> = _state

    fun getMember(memberId: Long) {
        viewModelScope.launch {
            _state.value = MyPageUiState.Loading
            try {
                val response = repository.getMember(memberId)
                if (response.isSuccessful) {
                    _state.value = MyPageUiState.Success(response.body()!!)
                } else {
                    _state.value = MyPageUiState.Error("정보 불러오기 실패")
                }
            } catch (e: Exception) {
                _state.value = MyPageUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }
}

class MyPageViewModelFactory(private val repository: MemberRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
