package com.oyn.rencangku.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    // Sudah ada versi by city
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // Tambahkan versi by koordinat
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoord(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = ApiConfig.WEATHER_API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

