package com.oyn.rencangku.ui.home

import android.util.Log
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.network.ApiClient

class HomeRepository(
    private val sessionManager: SessionManager
) {

    suspend fun getCulinaryPlaces(): List<CulinaryPlace> {
        val apiKey = ApiClient.API_KEY
        val auth = "Bearer $apiKey"

        val response = ApiClient.RestaurantApi.getCulinaryPlaces(
            apiKey = apiKey,
            auth = auth
        )
        Log.d("API_CHECK", "FULL RESPONSE: $response")
        Log.d("API_CHECK", "API result size: ${response.size}")
        return response
    }
}
