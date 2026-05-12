package com.oyn.rencangku.ml

import retrofit2.http.GET
import retrofit2.http.Query

interface MLApiService {

    @GET("recommend")
    suspend fun getRecommendations(
        @Query("user_id") userId: Int,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("weather") weather: String
    ): RecommendationResponse
}