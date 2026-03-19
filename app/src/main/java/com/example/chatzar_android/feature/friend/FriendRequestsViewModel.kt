package com.example.chatzar_android.feature.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.FriendshipResponse
import com.example.chatzar_android.data.repository.FriendshipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FriendRequestsUiState {
    object Idle : FriendRequestsUiState()
    object Loading : FriendRequestsUiState()
    data class Success(val requests: List<FriendshipResponse>) : FriendRequestsUiState()
    object AcceptSuccess : FriendRequestsUiState()
    data class Error(val message: String) : FriendRequestsUiState()
}

class FriendRequestsViewModel(private val repository: FriendshipRepository) : ViewModel() {

    private val _state = MutableStateFlow<FriendRequestsUiState>(FriendRequestsUiState.Idle)
    val state: StateFlow<FriendRequestsUiState> = _state

    fun getPendingRequests() {
        viewModelScope.launch {
            _state.value = FriendRequestsUiState.Loading
            try {
                val response = repository.getPendingRequests()
                if (response.isSuccessful) {
                    _state.value = FriendRequestsUiState.Success(response.body() ?: emptyList())
                } else {
                    _state.value = FriendRequestsUiState.Error("목록 로딩 실패")
                }
            } catch (e: Exception) {
                _state.value = FriendRequestsUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    fun acceptRequest(friendshipId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.acceptFriendRequest(friendshipId)
                if (response.isSuccessful) {
                    _state.value = FriendRequestsUiState.AcceptSuccess
                    getPendingRequests() // 목록 새로고침
                } else {
                    _state.value = FriendRequestsUiState.Error("수락 실패")
                }
            } catch (e: Exception) {
                _state.value = FriendRequestsUiState.Error("네트워크 오류")
            }
        }
    }
}

class FriendRequestsViewModelFactory(private val repository: FriendshipRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendRequestsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendRequestsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
