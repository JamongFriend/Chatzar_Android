package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.ChatApi
import com.example.chatzar_android.data.remote.dto.ChatRoomResponse
import retrofit2.Response

class ChatRepository(private val chatApi: ChatApi) {

    suspend fun getMyChatRooms(): Response<List<ChatRoomResponse>> {
        return chatApi.getMyChatRooms()
    }

    suspend fun getChatRoom(roomId: Long): Response<ChatRoomResponse> {
        return chatApi.getChatRoom(roomId)
    }

    suspend fun deleteChatRoom(roomId: Long): Response<Unit> {
        return chatApi.deleteChatRoom(roomId)
    }
}
