package com.example.chatzar_android.data.remote.dto

data class FriendshipResponse(
    val friendshipId: Long,
    val requesterId: Long,
    val requesterName: String,
    val requesterTag: String,
    val requesterAge: Long,
    val status: String // PENDING, ACCEPTED
)
