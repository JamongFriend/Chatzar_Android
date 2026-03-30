package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.MemberResponse
import com.example.chatzar_android.data.remote.dto.ProfileUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MemberApi {
    @GET("api/v1/members/{memberId}")
    suspend fun getMember(
        @Path("memberId") memberId: Long
    ): Response<MemberResponse>

    @PUT("api/v1/members/{memberId}")
    suspend fun updateMember(
        @Path("memberId") memberId: Long,
        @Body request: ProfileUpdateRequest
    ): Response<MemberResponse>
}
