package com.example.chatzar_android.feature.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.MemberResponse
import com.example.chatzar_android.data.remote.dto.ProfileUpdateRequest
import com.example.chatzar_android.data.repository.MemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileEditUiState {
    object Idle : ProfileEditUiState()
    object Loading : ProfileEditUiState()
    data class LoadSuccess(val member: MemberResponse) : ProfileEditUiState()
    object UpdateSuccess : ProfileEditUiState()
    data class Error(val message: String) : ProfileEditUiState()
}

class ProfileEditViewModel(private val repository: MemberRepository) : ViewModel() {

    private val _state = MutableStateFlow<ProfileEditUiState>(ProfileEditUiState.Idle)
    val state: StateFlow<ProfileEditUiState> = _state

    fun getMember(memberId: Long) {
        viewModelScope.launch {
            _state.value = ProfileEditUiState.Loading
            try {
                val response = repository.getMember(memberId)
                if (response.isSuccessful) {
                    _state.value = ProfileEditUiState.LoadSuccess(response.body()!!)
                } else {
                    _state.value = ProfileEditUiState.Error("정보 불러오기 실패")
                }
            } catch (e: Exception) {
                _state.value = ProfileEditUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    fun updateMember(memberId: Long, nickname: String) {
        viewModelScope.launch {
            _state.value = ProfileEditUiState.Loading
            try {
                val request = ProfileUpdateRequest(nickname)
                val response = repository.updateMember(memberId, request)
                if (response.isSuccessful) {
                    _state.value = ProfileEditUiState.UpdateSuccess
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "수정 실패"
                    _state.value = ProfileEditUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _state.value = ProfileEditUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }
}

class ProfileEditViewModelFactory(private val repository: MemberRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
