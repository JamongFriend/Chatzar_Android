package com.example.chatzar_android.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.ChatRoomResponse
import com.example.chatzar_android.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ChatListUiState {
    object Idle : ChatListUiState()
    object Loading : ChatListUiState()
    data class Success(val rooms: List<ChatRoomResponse>) : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
}

class ChatListViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _state = MutableStateFlow<ChatListUiState>(ChatListUiState.Idle)
    val state: StateFlow<ChatListUiState> = _state

    fun getMyChatRooms() {
        viewModelScope.launch {
            _state.value = ChatListUiState.Loading
            try {
                val response = repository.getMyChatRooms()
                if (response.isSuccessful) {
                    _state.value = ChatListUiState.Success(response.body() ?: emptyList())
                } else {
                    _state.value = ChatListUiState.Error("목록 로딩 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = ChatListUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }
}

class ChatListViewModelFactory(private val repository: ChatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
