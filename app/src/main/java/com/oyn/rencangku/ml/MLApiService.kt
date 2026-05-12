package com.oyn.rencangku.ml

import com.oyn.rencangku.data.CulinaryPlace
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MLApiService {

    /**
     * POST /recommend
     *
     * app.py menerima JSON body (RecommendRequest), bukan query param.
     * Response: RecommendResponse { user_id, mode, weather, total_returned, recommendations }
     */
    @POST("recommend")
    suspend fun getRecommendations(
        @Body request: RecommendRequest
    ): RecommendResponse

    /**
     * GET /places
     *
     * Ambil semua culinary places dari ML API (dari culinary_data.pkl).
     * Mendukung pagination dan filter kategori.
     * Response: PlacesResponse { total, skip, limit, places }
     */
    @GET("places")
    suspend fun getPlaces(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null
    ): PlacesResponse

    /**
     * GET /places/{id}
     *
     * Detail satu culinary place berdasarkan ID.
     */
    @GET("places/{id}")
    suspend fun getPlaceDetail(
        @Path("id") id: Int
    ): CulinaryPlace

    /**
     * GET /weather
     *
     * Cek kondisi cuaca di koordinat tertentu.
     * Response: { latitude, longitude, weather_condition }
     */
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse
}