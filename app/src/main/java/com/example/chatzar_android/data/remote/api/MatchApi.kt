package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.MatchConditionRequest
import com.example.chatzar_android.data.remote.dto.MatchResponse
import retrofit2.Response
import retrofit2.http.*

interface MatchApi {
    // 매칭 조건 설정 업데이트
    @POST("api/v1/match-preference/preference")
    suspend fun updatePreference(
        @Body request: MatchConditionRequest
    ): Response<Unit>

    // 매칭 조건 조회
    @GET("api/v1/match-preference/preference")
    suspend fun getPreference(): Response<MatchConditionRequest>

    // 매칭 요청 시작 (즉시 매칭될 수도 있음)
    @POST("api/v1/match/request")
    suspend fun requestMatch(): Response<MatchResponse>

    // 매칭 요청 취소
    @DELETE("api/v1/match/cancel")
    suspend fun cancelMatch(): Response<Unit>

    // 매칭 상태 조회 (WAITING, MATCHED 여부 포함)
    @GET("api/v1/match/status")
    suspend fun getMatchStatus(): Response<MatchResponse>
}
