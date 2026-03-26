package com.example.chatzar_android.feature.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.FriendListResponse
import com.example.chatzar_android.data.remote.dto.FriendshipResponse
import com.example.chatzar_android.data.repository.FriendshipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FriendUiState {
    object Loading : FriendUiState()
    data class Success(
        val friends: List<FriendListResponse>,
        val pendingRequests: List<FriendshipResponse>
    ) : FriendUiState()
    data class Error(val message: String) : FriendUiState()
}

class FriendViewModel(private val repository: FriendshipRepository) : ViewModel() {

    private val _state = MutableStateFlow<FriendUiState>(FriendUiState.Loading)
    val state: StateFlow<FriendUiState> = _state

    fun fetchFriendData() {
        viewModelScope.launch {
            _state.value = FriendUiState.Loading
            try {
                val friendResponse = repository.getFriendList()
                val pendingResponse = repository.getPendingRequests()

                if (friendResponse.isSuccessful && pendingResponse.isSuccessful) {
                    _state.value = FriendUiState.Success(
                        friends = friendResponse.body() ?: emptyList(),
                        pendingRequests = pendingResponse.body() ?: emptyList()
                    )
                } else {
                    _state.value = FriendUiState.Error("데이터를 불러오지 못했습니다.")
                }
            } catch (e: Exception) {
                _state.value = FriendUiState.Error(e.message ?: "오류 발생")
            }
        }
    }

    fun acceptRequest(friendshipId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.acceptFriendRequest(friendshipId)
                if (response.isSuccessful) {
                    fetchFriendData() // 새로고침
                } else {
                    // 에러 처리
                }
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }
}

class FriendViewModelFactory(private val repository: FriendshipRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
