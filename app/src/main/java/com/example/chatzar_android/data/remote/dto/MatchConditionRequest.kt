package com.example.chatzar_android.data.remote.dto

data class MatchConditionRequest(
    val genderPreference: String?, // ANY, MALE, FEMALE
    val minAge: Int?,
    val maxAge: Int?,
    val topic: String?,
    val region: String?
)
