package com.oyn.rencangku.ui.home

import android.util.Log
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.network.ApiClient

class HomeRepository(
    private val sessionManager: SessionManager
) {
    val apiKey = ApiClient.API_KEY
    val auth = "Bearer $apiKey"

    suspend fun getCulinaryPlaces(): List<CulinaryPlace> {

        val response = ApiClient.RestaurantApi.getCulinaryPlaces(
            apiKey = apiKey,
            auth = auth
        )
        Log.d("API_CHECK", "FULL RESPONSE: $response")
        Log.d("API_CHECK", "API result size: ${response.size}")
        return response
    }

    suspend fun getCategory(category: String): List<CulinaryPlace> {
        return ApiClient.RestaurantApi.getCulinaryPlacesByCategory(
            apiKey = apiKey,
            auth = auth,
            category = "eq.$category"
        )
    }
}
