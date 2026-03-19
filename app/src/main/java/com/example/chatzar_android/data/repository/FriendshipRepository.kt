package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.FriendshipApi
import com.example.chatzar_android.data.remote.dto.FriendshipResponse
import retrofit2.Response

class FriendshipRepository(private val api: FriendshipApi) {
    suspend fun sendFriendRequest(targetId: Long): Response<Void> {
        return api.sendFriendRequest(targetId)
    }

    suspend fun getPendingRequests(): Response<List<FriendshipResponse>> {
        return api.getPendingRequests()
    }

    suspend fun acceptFriendRequest(friendshipId: Long): Response<Void> {
        return api.acceptFriendRequest(friendshipId)
    }
}
