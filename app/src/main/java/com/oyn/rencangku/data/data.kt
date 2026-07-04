package com.oyn.rencangku.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class CulinaryPlace(

    // ── ID ─────────────────────────────────────────────────────
    // Supabase pakai "id", ML API juga pakai "id"
    @SerializedName("id")
    val id: Int? = null,

    // ── NAMA ───────────────────────────────────────────────────
    // Supabase: "Title", ML API: "title"
    @SerializedName("Title")
    val titleSupabase: String? = null,

    @SerializedName("title")
    val titleMl: String? = null,

    // ── KATEGORI ───────────────────────────────────────────────
    @SerializedName("Category")
    val categorySupabase: String? = null,

    @SerializedName("category")
    val categoryMl: String? = null,

    // ── ALAMAT ─────────────────────────────────────────────────
    @SerializedName("Address")
    val addressSupabase: String? = null,

    @SerializedName("address")
    val addressMl: String? = null,

    // ── RATING ─────────────────────────────────────────────────
    @SerializedName("Rating")
    val ratingSupabase: Double? = null,

    @SerializedName("rating")
    val ratingMl: Double? = null,

    // ── JUMLAH RATING ──────────────────────────────────────────
    @SerializedName("Rating_count")
    val ratingCountSupabase: Int? = null,

    @SerializedName("rating_count")
    val ratingCountMl: Int? = null,

    // ── HARGA ──────────────────────────────────────────────────
    @SerializedName("Price_range")
    val priceRangeSupabase: String? = null,

    @SerializedName("price_range")
    val priceRangeMl: String? = null,

    @SerializedName("price_category")
    val priceCategory: String? = null,       // hanya ada di ML API

    // ── KOORDINAT ──────────────────────────────────────────────
    @SerializedName("Latitude")
    val latitudeSupabase: Double? = null,

    @SerializedName("latitude")
    val latitudeMl: Double? = null,

    @SerializedName("Longitude")
    val longitudeSupabase: Double? = null,

    @SerializedName("longitude")
    val longitudeMl: Double? = null,

    // ── CUACA ──────────────────────────────────────────────────
    @SerializedName("Categorize_Weather")
    val weatherSupabase: String? = null,

    @SerializedName("categorize_weather")
    val weatherMl: String? = null,

    // ── HANYA SUPABASE ─────────────────────────────────────────
    @SerializedName("URL")
    val pageUrl: String? = null,

    @SerializedName("Header_image")
    val headerImage: String? = null,

    @SerializedName("Phone")
    val phone: String? = null,

    @SerializedName("Open_hours")
    val openHours: Map<String, List<String>>? = null,

    // ── HANYA ML API ───────────────────────────────────────────
    @SerializedName("about")
    val about: String? = null,

    @SerializedName("menu")
    val menu: String? = null,

    @SerializedName("popularity_score")
    val popularityScore: Double? = null,

    @SerializedName("recommendation_score")
    val recommendationScore: Double? = null,  // hanya ada di /recommend
) : Parcelable {
    // ── Helper properties: pakai nilai dari sumber mana pun yang tersedia ──

    val displayTitle: String
        get() = titleMl ?: titleSupabase ?: ""

    val displayCategory: String
        get() = categoryMl ?: categorySupabase ?: ""

    val displayAddress: String
        get() = addressMl ?: addressSupabase ?: ""

    val displayRating: Double
        get() = ratingMl ?: ratingSupabase ?: 0.0

    val displayRatingCount: Int
        get() = ratingCountMl ?: ratingCountSupabase ?: 0

    val displayPriceRange: String
        get() = priceRangeMl ?: priceRangeSupabase ?: ""

    val displayLatitude: Double?
        get() = latitudeMl ?: latitudeSupabase

    val displayLongitude: Double?
        get() = longitudeMl ?: longitudeSupabase

    val displayWeather: String
        get() = weatherMl ?: weatherSupabase ?: ""
}

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

data class UserPreferenceRequest(
    @SerializedName("users_id")
    val usersId: Int,

    @SerializedName("favorite_category")
    val favoriteCategory: String,

    @SerializedName("favorite_price")
    val favoritePrice: String,

    @SerializedName("favorite_rating")
    val favoriteRating: Float,

    @SerializedName("favorite_open_time")
    val favoriteOpenTime: String,

    @SerializedName("favorite_weather")
    val favoriteWeather: String
)

// ─── Response dari Supabase ───────────────────────────────────
data class UserPreferenceResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("users_id")
    val usersId: Int,

    @SerializedName("favorite_category")
    val favoriteCategory: String?,

    @SerializedName("favorite_price")
    val favoritePrice: String?,

    @SerializedName("favorite_rating")
    val favoriteRating: Float?,

    @SerializedName("favorite_open_time")
    val favoriteOpenTime: String?,

    @SerializedName("favorite_weather")
    val favoriteWeather: String?
)

data class CheckPreferenceResponse(
    @SerializedName("exists")
    val exists: Boolean,

    @SerializedName("data")
    val data: UserPreferenceResponse? = null
)

data class Review(
    val id: Int,
    val created_at: String,
    val users_id: Int,
    val culinary_places_id: Int,
    val rating: Int,
    val review_text: String?,
    val photos: String?,
    val users: User?
)

data class ReviewRequest(
    val users_id: Int,
    val culinary_places_id: Int,
    val rating: Int,
    val review_text: String?,
    val photos: String?
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