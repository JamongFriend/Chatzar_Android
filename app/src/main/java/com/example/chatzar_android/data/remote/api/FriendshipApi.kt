package com.example.chatzar_android.data.remote.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendshipApi {
    @POST("api/v1/friends/request/{targetId}")
    suspend fun sendFriendRequest(
        @Path("targetId") targetId: Long
    ): Response<Void>
}
