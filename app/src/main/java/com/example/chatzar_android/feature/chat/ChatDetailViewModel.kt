package com.example.chatzar_android.feature.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.ChatRoomResponse
import com.example.chatzar_android.data.remote.dto.MessageResponse
import com.example.chatzar_android.data.remote.dto.SocketMessage
import com.example.chatzar_android.data.repository.ChatRepository
import com.example.chatzar_android.data.repository.FriendshipRepository
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
    data class RoomStatusSuccess(val room: ChatRoomResponse) : ChatDetailUiState()
    data class HistorySuccess(val messages: List<MessageResponse>) : ChatDetailUiState()
    data class NewMessage(val message: MessageResponse) : ChatDetailUiState()
    object FriendRequestSuccess : ChatDetailUiState()
    data class Error(val message: String) : ChatDetailUiState()
}

class ChatDetailViewModel(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val friendshipRepository: FriendshipRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ChatDetailUiState>(ChatDetailUiState.Idle)
    val state: StateFlow<ChatDetailUiState> = _state

    private var stompClient: StompClient? = null
    private val gson = Gson()

    // 친구 신청 보내기
    fun sendFriendRequest(targetId: Long) {
        viewModelScope.launch {
            try {
                val response = friendshipRepository.sendFriendRequest(targetId)
                if (response.isSuccessful) {
                    _state.value = ChatDetailUiState.FriendRequestSuccess
                } else {
                    _state.value = ChatDetailUiState.Error("친구 신청 실패")
                }
            } catch (e: Exception) {
                _state.value = ChatDetailUiState.Error("네트워크 오류")
            }
        }
    }

    // 방 정보 및 상태 조회
    fun getChatRoom(roomId: Long) {
        viewModelScope.launch {
            try {
                val response = chatRepository.getChatRoom(roomId)
                if (response.isSuccessful) {
                    _state.value = ChatDetailUiState.RoomStatusSuccess(response.body()!!)
                }
            } catch (e: Exception) {
                _state.value = ChatDetailUiState.Error("방 정보 로딩 실패")
            }
        }
    }

    // 이전 메시지 로딩
    fun getMessages(roomId: Long) {
        viewModelScope.launch {
            _state.value = ChatDetailUiState.Loading
            try {
                val response = messageRepository.getRecentMessages(roomId)
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
        if (stompClient != null && stompClient!!.isConnected) return

        stompClient = messageRepository.connectStomp(wsUrl)

        // 토픽 구독 시 에러 핸들러 추가
        stompClient?.topic("/sub/rooms/$roomId")?.subscribe({ stompMessage ->
            val message = gson.fromJson(stompMessage.payload, MessageResponse::class.java)
            viewModelScope.launch {
                _state.value = ChatDetailUiState.NewMessage(message)
            }
        }, { error ->
            Log.e("ChatDetailViewModel", "Topic Subscription Error", error)
            viewModelScope.launch {
                _state.value = ChatDetailUiState.Error("실시간 메시지 수신에 실패했습니다.")
            }
        })

        // 연결 상태 모니터링 및 에러 핸들러 추가
        stompClient?.lifecycle()?.subscribe({ lifecycleEvent ->
            when (lifecycleEvent.type) {
                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED -> {
                    Log.d("ChatDetailViewModel", "Stomp Connection Opened")
                }
                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR -> {
                    Log.e("ChatDetailViewModel", "Stomp Connection Error", lifecycleEvent.exception)
                }
                ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED -> {
                    Log.d("ChatDetailViewModel", "Stomp Connection Closed")
                }
                else -> {}
            }
        }, { error ->
            Log.e("ChatDetailViewModel", "Stomp Lifecycle Error", error)
        })
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
        messageRepository.disconnectStomp()
    }
}

class ChatDetailViewModelFactory(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val friendshipRepository: FriendshipRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatDetailViewModel(messageRepository, chatRepository, friendshipRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
