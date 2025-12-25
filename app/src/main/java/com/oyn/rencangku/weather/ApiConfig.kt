package com.oyn.rencangku.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    const val WEATHER_API_KEY = "8c79ecf19f5084f74baa0c841a95214f"
    private const val BASE_URL = "https://api.openweathermap.org/"

    val weatherService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
