package com.oyn.rencangku.ml

import com.google.gson.annotations.SerializedName

data class RecommendationItem(

    @SerializedName("culinary_places_id")
    val id: Int,

    @SerializedName("Title")
    val title: String,

    @SerializedName("Category")
    val category: String?,

    @SerializedName("Rating")
    val rating: Double?,

    @SerializedName("URL")
    val page_url: String?,

    @SerializedName("Header_image")
    val header_image: String?,

    @SerializedName("Rating_count")
    val rating_count: Int?,

    @SerializedName("Price_range")
    val price_range: String?,

    @SerializedName("Address")
    val address: String?,

    @SerializedName("Latitude")
    val latitude: Double?,

    @SerializedName("Longitude")
    val longitude: Double?,

    @SerializedName("Phone")
    val phone: String?,

    @SerializedName("Open_hours")
    val open_hours: Map<String, List<String>>?,

    @SerializedName("Categorize_Weather")
    val categorize_weather: String?
)