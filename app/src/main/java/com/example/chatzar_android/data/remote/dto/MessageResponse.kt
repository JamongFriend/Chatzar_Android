package com.example.chatzar_android.data.remote.dto

import java.time.LocalDateTime

data class MessageResponse(
    val messageId: Long,
    val roomId: Long,
    val senderId: Long,
    val senderNickname: String,
    val content: String,
    val createdAt: String
)

data class SocketMessage(
    val type: String, // JOIN, SEND
    val roomId: Long,
    val content: String? = null
)
