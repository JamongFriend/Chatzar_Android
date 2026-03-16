package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.MessageApi
import com.example.chatzar_android.data.remote.dto.MessageResponse
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompMessage

class MessageRepository(private val messageApi: MessageApi) {

    private var stompClient: StompClient? = null

    // 이전 메시지 내역 조회
    suspend fun getRecentMessages(roomId: Long): Response<List<MessageResponse>> {
        return messageApi.getRecentMessages(roomId)
    }

    // STOMP WebSocket 연결
    fun connectStomp(url: String, accessToken: String? = null): StompClient {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        
        // 헤더에 토큰 추가 (필요 시)
        val headers = mutableListOf<ua.naiksoftware.stomp.dto.StompHeader>()
        accessToken?.let {
            headers.add(ua.naiksoftware.stomp.dto.StompHeader("Authorization", "Bearer $it"))
        }

        stompClient?.connect(headers)
        return stompClient!!
    }

    // STOMP 연결 해제
    fun disconnectStomp() {
        stompClient?.disconnect()
    }
}
