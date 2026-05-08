package com.oyn.rencangku.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class CulinaryPlace(
    val id: Int,

    @SerializedName("URL")
    val page_url: String?,

    @SerializedName("Title")
    val title: String,

    @SerializedName("Header_image")
    val header_image: String?,

    @SerializedName("Rating")
    val rating: Double?,

    @SerializedName("Rating_count")
    val rating_count: Int?,

    @SerializedName("Price_range")
    val price_range: String?,

    @SerializedName("Category")
    val category: String?,

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
) : Parcelable

data class Favorite(
    val id: Int,
    val created_at: String,
    val users_id: Int,
    val culinary_places_id: Int,

    @SerializedName("culinary_places")
    val _culinary_places: CulinaryPlace?
)

data class FavoriteRequest(
    val users_id: Int,
    val culinary_places_id: Int
)

data class Review(
    val id: Int,
    val created_at: String,
    val users_id: Int,
    val culinary_places_id: Int,
    val rating: Int,
    val review_text: String?,
    val photos: String?
)

data class ReviewRequest(
    val users_id: Int,
    val culinary_places_id: Int,
    val rating: Int,
    val review_text: String?
)

data class UserInteraction(
    val id: Int,
    val created_at: String,
    val users_id: Int,
    val culinary_places_id: Int,
    val interaction_value: Double
)

data class UserInteractionRequest(
    val users_id: Int,
    val culinary_places_id: Int,
    val interaction_value: Double
)


data class User(
    val id: Int,
    val name: String,
    val email: String,
    val created_at: String? = null,
    val avatar: String? = null
)