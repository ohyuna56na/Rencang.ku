package com.oyn.rencangku.ui.favorite

import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.Favorite
import com.oyn.rencangku.data.FavoriteRequest
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.network.ApiService

class FavoriteRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    private fun getApiKey(): String {
        return ApiClient.API_KEY
    }

    private fun getUserId(): String {
        val userId = sessionManager.getUserId()
        if (userId == -1) throw IllegalStateException("User belum login")
        return userId.toString()
    }

    private fun getAuth(): String {
        return "Bearer ${ApiClient.API_KEY}"
    }

    suspend fun getFavorites(): List<Favorite> {
        return apiService.getFavorites(
            apiKey = getApiKey(),
            auth = getAuth(),
            userId = "eq.${getUserId()}"
        )
    }

    suspend fun deleteFavorite(id: Int) {
        apiService.deleteFavorite(
            apiKey = getApiKey(),
            auth = getAuth(),
            userId = getUserId(),
            id = "eq.$id"
        )
    }
}