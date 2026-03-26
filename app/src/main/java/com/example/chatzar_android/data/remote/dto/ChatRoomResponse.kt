package com.example.chatzar_android.data.remote.dto

data class ChatRoomResponse(
    val roomId: Long,
    val otherMemberId: Long,
    val otherNickname: String,
    val status: String
)
