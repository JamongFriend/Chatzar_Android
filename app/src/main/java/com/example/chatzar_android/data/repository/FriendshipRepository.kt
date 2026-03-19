package com.example.chatzar_android.data.repository

import com.example.chatzar_android.data.remote.api.FriendshipApi
import retrofit2.Response

class FriendshipRepository(private val api: FriendshipApi) {
    suspend fun sendFriendRequest(targetId: Long): Response<Void> {
        return api.sendFriendRequest(targetId)
    }
}
