package com.example.chatzar_android.data.remote.dto

data class MatchResponse(
    val matched: Boolean,
    val matchId: Long?,
    val chatRoomId: Long?,
    val partnerId: Long?,
    val partnerNickname: String?
)
