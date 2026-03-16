package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.MatchApi
import com.example.chatzar_android.data.remote.dto.MatchConditionRequest
import com.example.chatzar_android.data.remote.dto.MatchResponse
import retrofit2.Response

class MatchRepository(private val matchApi: MatchApi) {

    suspend fun updatePreference(request: MatchConditionRequest): Response<Unit> {
        return matchApi.updatePreference(request)
    }

    suspend fun requestMatch(): Response<MatchResponse> {
        return matchApi.requestMatch()
    }

    suspend fun cancelMatch(): Response<Unit> {
        return matchApi.cancelMatch()
    }

    suspend fun getMatchStatus(): Response<MatchResponse> {
        return matchApi.getMatchStatus()
    }
}
