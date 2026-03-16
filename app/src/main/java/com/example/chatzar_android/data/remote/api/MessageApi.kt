package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.MessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApi {
    // 최근 메시지 조회
    @GET("chat-rooms/{roomId}/messages/recent")
    suspend fun getRecentMessages(
        @Path("roomId") roomId: Long
    ): Response<List<MessageResponse>>

    // 이전 메시지 더보기 (페이지네이션)
    @GET("chat-rooms/{roomId}/messages")
    suspend fun getOlderMessages(
        @Path("roomId") roomId: Long,
        @Query("before") beforeMessageId: Long
    ): Response<List<MessageResponse>>
}
