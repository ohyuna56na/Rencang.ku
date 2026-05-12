package com.oyn.rencangku.data

import com.oyn.rencangku.ml.MLApiClient
import com.oyn.rencangku.ml.RecommendRequest
import com.oyn.rencangku.ml.RecommendResponse
import com.oyn.rencangku.ml.PlacesResponse
import com.oyn.rencangku.network.ApiClient

/**
 * Repository tunggal untuk semua data culinary place.
 *
 * Sumber:
 *  - ML API  → rekomendasi personal + daftar semua tempat (dari .pkl)
 *  - Supabase → data detail lengkap (URL, Header_image, Phone, Open_hours)
 *
 * Gunakan ML API untuk tampilan home/rekomendasi.
 * Gunakan Supabase untuk tampilan detail (ada gambar, jam buka, dll).
 */
class CulinaryRepository {

    private val mlApi = MLApiClient.api
    private val supabaseApi = ApiClient.RestaurantApi
    private val apiKey = ApiClient.API_KEY
    private val authHeader = "Bearer $apiKey"

    // =========================================================
    // DARI ML API
    // =========================================================

    /**
     * Ambil rekomendasi personal untuk user.
     * Gunakan userId = 0 untuk user baru / cold start.
     */
    suspend fun getRecommendations(
        userId: Int,
        latitude: Double,
        longitude: Double,
        topN: Int = 10,
        autoWeather: Boolean = true,
        weather: String? = null
    ): RecommendResponse {
        return mlApi.getRecommendations(
            RecommendRequest(
                userId = userId,
                latitude = latitude,
                longitude = longitude,
                topN = topN,
                autoWeather = autoWeather,
                weather = weather
            )
        )
    }

    /**
     * Ambil semua culinary places dari ML API dengan pagination.
     * Data dari file culinary_data.pkl (hasil training dari Xano).
     */
    suspend fun getPlacesFromMl(
        skip: Int = 0,
        limit: Int = 20,
        category: String? = null
    ): PlacesResponse {
        return mlApi.getPlaces(skip = skip, limit = limit, category = category)
    }

    /**
     * Ambil detail satu tempat dari ML API.
     */
    suspend fun getPlaceDetailFromMl(id: Int): CulinaryPlace {
        return mlApi.getPlaceDetail(id)
    }

    // =========================================================
    // DARI SUPABASE
    // =========================================================

    /**
     * Ambil semua culinary places dari Supabase.
     * Lebih lengkap: ada Header_image, Phone, Open_hours, URL.
     */
    suspend fun getPlacesFromSupabase(): List<CulinaryPlace> {
        return supabaseApi.getCulinaryPlaces(
            apiKey = apiKey,
            auth = authHeader
        )
    }

    /**
     * Ambil detail satu tempat dari Supabase berdasarkan ID.
     */
    suspend fun getPlaceDetailFromSupabase(id: Int, userId: String): CulinaryPlace {
        return supabaseApi.getCulinaryPlaceDetail(
            apiKey = apiKey,
            auth = authHeader,
            userId = userId,
            id = id
        )
    }
}