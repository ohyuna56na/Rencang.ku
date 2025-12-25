package com.oyn.rencangku.auth

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        const val TOKEN = "TOKEN"
        const val IS_LOGIN = "IS_LOGIN"
        const val ONBOARDING_SHOWN = "ONBOARDING_SHOWN"
    }

    fun saveLogin(token: String) {
        prefs.edit()
            .putString(TOKEN, token)
            .putBoolean(IS_LOGIN, true)
            .apply()
    }

    fun isLogin(): Boolean {
        return prefs.getBoolean(IS_LOGIN, false)
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, null)
    }

    fun setOnboardingShown(shown: Boolean) {
        prefs.edit().putBoolean(ONBOARDING_SHOWN, shown).apply()
    }

    fun isOnboardingShown(): Boolean {
        return prefs.getBoolean(ONBOARDING_SHOWN, false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
