package com.oyn.rencangku.ui.favorite

import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.Favorite
import com.oyn.rencangku.network.ApiService

class FavoriteRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    private fun getAuthToken(): String {
        val token = sessionManager.getToken()
            ?: throw IllegalStateException("Token tidak ditemukan")

        return "Bearer $token"
    }

    suspend fun getFavorites(): List<Favorite> {
        return apiService.getFavorites(getAuthToken())
    }

    suspend fun deleteFavorite(id: Int) {
        apiService.deleteFavorite(getAuthToken(), id)
    }
}