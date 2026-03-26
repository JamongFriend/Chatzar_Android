package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.ChatRoomResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {
    // 내 채팅방 목록 조회
    @GET("api/v1/chat-rooms")
    suspend fun getMyChatRooms(): Response<List<ChatRoomResponse>>

    // 특정 채팅방 정보 조회
    @GET("api/v1/chat-rooms/{roomId}")
    suspend fun getChatRoom(
        @Path("roomId") roomId: Long
    ): Response<ChatRoomResponse>

    // 채팅방 나가기/삭제
    @DELETE("api/v1/chat-rooms/{roomId}")
    suspend fun deleteChatRoom(
        @Path("roomId") roomId: Long
    ): Response<Unit>

    // 채팅방 닫기 (잠금)
    @POST("api/v1/chat-rooms/{roomId}/close")
    suspend fun closeChatRoom(
        @Path("roomId") roomId: Long
    ): Response<Unit>
}
