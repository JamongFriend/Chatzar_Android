package com.example.chatzar_android.feature.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.MessageResponse
import com.example.chatzar_android.data.remote.dto.SocketMessage
import com.example.chatzar_android.data.repository.MessageRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompMessage

sealed class ChatDetailUiState {
    object Idle : ChatDetailUiState()
    object Loading : ChatDetailUiState()
    data class HistorySuccess(val messages: List<MessageResponse>) : ChatDetailUiState()
    data class NewMessage(val message: MessageResponse) : ChatDetailUiState()
    data class Error(val message: String) : ChatDetailUiState()
}

class ChatDetailViewModel(private val repository: MessageRepository) : ViewModel() {

    private val _state = MutableStateFlow<ChatDetailUiState>(ChatDetailUiState.Idle)
    val state: StateFlow<ChatDetailUiState> = _state

    private var stompClient: StompClient? = null
    private val gson = Gson()

    // 이전 메시지 로딩
    fun getMessages(roomId: Long) {
        viewModelScope.launch {
            _state.value = ChatDetailUiState.Loading
            try {
                val response = repository.getRecentMessages(roomId)
                if (response.isSuccessful) {
                    _state.value = ChatDetailUiState.HistorySuccess(response.body() ?: emptyList())
                } else {
                    _state.value = ChatDetailUiState.Error("이전 대화 로딩 실패")
                }
            } catch (e: Exception) {
                _state.value = ChatDetailUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    // WebSocket 연결 및 구독
    fun connectAndSubscribe(roomId: Long, wsUrl: String) {
        stompClient = repository.connectStomp(wsUrl)
        
        // 구독 (/sub/rooms/{roomId})
        stompClient?.topic("/sub/rooms/$roomId")?.subscribe { stompMessage ->
            val message = gson.fromJson(stompMessage.payload, MessageResponse::class.java)
            viewModelScope.launch {
                _state.value = ChatDetailUiState.NewMessage(message)
            }
        }

        // 연결 상태 모니터링
        stompClient?.lifecycle()?.subscribe { lifecycleEvent ->
            Log.d("ChatDetailViewModel", "Stomp Lifecycle: ${lifecycleEvent.type}")
        }
    }

    // 메시지 전송 (/pub/chat)
    fun sendMessage(roomId: Long, content: String) {
        val socketMessage = SocketMessage("SEND", roomId, content)
        val payload = gson.toJson(socketMessage)
        
        stompClient?.send("/pub/chat", payload)?.subscribe({
            Log.d("ChatDetailViewModel", "Message Sent: $content")
        }, { error ->
            Log.e("ChatDetailViewModel", "Send Error", error)
        })
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnectStomp()
    }
}

class ChatDetailViewModelFactory(private val repository: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
