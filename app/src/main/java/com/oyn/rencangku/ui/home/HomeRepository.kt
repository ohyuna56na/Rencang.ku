package com.oyn.rencangku.ui.home

import android.util.Log
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.network.ApiClient

class HomeRepository {

    suspend fun getCulinaryPlaces(): List<CulinaryPlace> {
        val response = ApiClient.RestaurantApi.getCulinaryPlaces()
        Log.d("API_CHECK", "API result size: ${response.size}")
        Log.d("API_CHECK", "API result data: $response")
        return response
    }
}
