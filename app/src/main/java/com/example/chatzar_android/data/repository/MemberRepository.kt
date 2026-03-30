package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.MemberApi
import com.example.chatzar_android.data.remote.dto.MemberResponse
import com.example.chatzar_android.data.remote.dto.ProfileUpdateRequest
import retrofit2.Response

class MemberRepository(private val memberApi: MemberApi) {
    suspend fun getMember(memberId: Long): Response<MemberResponse> {
        return memberApi.getMember(memberId)
    }

    suspend fun updateMember(memberId: Long, request: ProfileUpdateRequest): Response<MemberResponse> {
        return memberApi.updateMember(memberId, request)
    }
}
