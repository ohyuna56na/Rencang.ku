package com.oyn.rencangku.ml

import com.google.gson.annotations.SerializedName
import com.oyn.rencangku.data.CulinaryPlace

// =========================================================
// REQUEST — POST /recommend
// Sesuai dengan RecommendRequest di app.py
// =========================================================
data class RecommendRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("top_n")
    val topN: Int = 10,

    /**
     * true  = ML API deteksi cuaca otomatis via koordinat (default)
     * false = pakai field [weather] di bawah
     */
    @SerializedName("auto_weather")
    val autoWeather: Boolean = true,

    /**
     * Override manual: "dingin" | "panas" | "semua"
     * Hanya dipakai jika autoWeather = false
     */
    @SerializedName("weather")
    val weather: String? = null
)

// =========================================================
// RESPONSE — POST /recommend
// Sesuai dengan RecommendResponse di app.py
// =========================================================
data class RecommendResponse(
    @SerializedName("user_id")
    val userId: Int,

    /** "cold_start" atau "warm (N interaksi)" */
    @SerializedName("mode")
    val mode: String,

    /** "dingin" | "panas" | "semua" */
    @SerializedName("weather")
    val weather: String,

    @SerializedName("total_returned")
    val totalReturned: Int,

    /**
     * List CulinaryPlace — field snake_case dari ML API.
     * Gunakan .displayTitle, .displayRating, dll untuk akses unified.
     */
    @SerializedName("recommendations")
    val recommendations: List<CulinaryPlace>
)

// =========================================================
// RESPONSE — GET /places
// Sesuai dengan endpoint list_places di app.py
// =========================================================
data class PlacesResponse(
    @SerializedName("total")
    val total: Int,

    @SerializedName("skip")
    val skip: Int,

    @SerializedName("limit")
    val limit: Int,

    /** List CulinaryPlace — field snake_case dari ML API */
    @SerializedName("places")
    val places: List<CulinaryPlace>
)

// =========================================================
// RESPONSE — GET /weather
// =========================================================
data class WeatherResponse(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    /** "dingin" | "panas" | "semua" */
    @SerializedName("weather_condition")
    val weatherCondition: String
)