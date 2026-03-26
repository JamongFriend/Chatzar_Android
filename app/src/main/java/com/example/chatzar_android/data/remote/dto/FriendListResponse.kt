package com.example.chatzar_android.data.remote.dto

data class FriendListResponse(
    val friendshipId: Long,
    val friendId: Long,
    val nickname: String,
    val tag: String,
    val age: Long
)
