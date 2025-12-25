package com.oyn.rencangku.ui.favorite

import com.oyn.rencangku.data.Favorite
import com.oyn.rencangku.network.ApiService

class FavoriteRepository(
    private val apiService: ApiService
) {

    suspend fun getFavorites(): List<Favorite> {
        return apiService.getFavorites()
    }

    suspend fun deleteFavorite(id: Int) {
        apiService.deleteFavorite(id)
    }
}
