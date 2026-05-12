package com.oyn.rencangku.ml

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MLApiClient {

    private const val BASE_URL =
        "https://najwaaulia05-API-Xano.hf.space/"

    val api: MLApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MLApiService::class.java)
    }
}