package com.example.project_praticum

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("plant_app_session", Context.MODE_PRIVATE)

    fun saveAuth(token: String, user: User) {
        prefs.edit()
            .putString("token", token)
            .putInt("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_phone", user.phone)
            .putString("user_avatar", user.avatar)
            .putString("user_avatar_url", user.avatar_url)
            .putString("user_role", user.role)
            .apply()
    }

    fun saveUser(user: User) {
        prefs.edit()
            .putInt("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_phone", user.phone)
            .putString("user_avatar", user.avatar)
            .putString("user_avatar_url", user.avatar_url)
            .putString("user_role", user.role)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun getBearerToken(): String? {
        val token = getToken()
        return if (token.isNullOrBlank()) null else "Bearer $token"
    }

    fun getUserName(): String {
        return prefs.getString("user_name", "Guest") ?: "Guest"
    }

    fun getUserEmail(): String {
        return prefs.getString("user_email", "") ?: ""
    }

    fun getUserPhone(): String {
        return prefs.getString("user_phone", "") ?: ""
    }

    fun getUserAvatarUrl(): String? {
        return prefs.getString("user_avatar_url", null)
    }

    fun getUserRole(): String {
        return prefs.getString("user_role", "customer") ?: "customer"
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}