package com.example.chatzar_android.data.remote.api

import com.example.chatzar_android.data.remote.dto.FriendshipResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendshipApi {
    @POST("api/v1/friends/request/{targetId}")
    suspend fun sendFriendRequest(
        @Path("targetId") targetId: Long
    ): Response<Void>

    @GET("api/v1/friends/pending")
    suspend fun getPendingRequests(): Response<List<FriendshipResponse>>

    @POST("api/v1/friends/accept/{friendshipId}")
    suspend fun acceptFriendRequest(
        @Path("friendshipId") friendshipId: Long
    ): Response<Void>

    @GET("api/v1/friends/list")
    suspend fun getFriendList(): Response<List<com.example.chatzar_android.data.remote.dto.FriendListResponse>>
}
