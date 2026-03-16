package com.example.chatzar_android.core.network

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("chatzar_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val MEMBER_ID = "member_id"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(ACCESS_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(ACCESS_TOKEN, null)
    }

    fun saveMemberId(id: Long) {
        prefs.edit().putLong(MEMBER_ID, id).apply()
    }

    fun getMemberId(): Long {
        return prefs.getLong(MEMBER_ID, -1L)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
