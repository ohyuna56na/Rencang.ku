package com.oyn.rencangku.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://xrchjsinkfbgnhweyign.supabase.co/rest/v1/"
    const val API_KEY ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhyY2hqc2lua2ZiZ25od2V5aWduIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc4NDc4NTYsImV4cCI6MjA5MzQyMzg1Nn0.xxoQotZJyp1XH8V3V1AW8BrvAisRWIjh_aOacCpGnxw"
    const val STORAGE_URL =
        "https://xrchjsinkfbgnhweyign.supabase.co/storage/v1/object"
    const val BUCKET = "review-images"

    val RestaurantApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}