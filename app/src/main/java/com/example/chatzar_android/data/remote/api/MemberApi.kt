package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.MemberResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MemberApi {
    @GET("api/v1/members/{memberId}")
    suspend fun getMember(
        @Path("memberId") memberId: Long
    ): Response<MemberResponse>
}
