package com.example.chatzar_android.data.remote.dto

data class FriendshipResponse(
    val friendshipId: Long,
    val requesterName: String,
    val status: String // PENDING, ACCEPTED, etc.
)
