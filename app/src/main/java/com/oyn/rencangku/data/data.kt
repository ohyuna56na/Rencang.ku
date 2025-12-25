package com.oyn.rencangku.data

data class CulinaryPlace(
    val id: Int,
    val page_url: String?,
    val title: String,
    val header_image: String?,
    val rating: String?,
    val rating_count: String?,
    val price_range: String?,
    val category: String?,
    val address: String?,
    val latitude: String?,
    val longitude: String?,
    val phone: String?,
    val open_hours: String?,
    val categorize_weather: String?
)

data class OpenHours(
    val monday: String?,
    val tuesday: String?,
    val wednesday: String?,
    val thursday: String?,
    val friday: String?,
    val saturday: String?,
    val sunday: String?
)

data class Favorite(
    val id: Int,
    val created_at: String,
    val users_id: Int,
    val culinary_places_id: Int
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
    val photos: String?,
    val visit_weather: VisitWeather
)

data class ReviewRequest(
    val users_id: Int,
    val culinary_places_id: Int,
    val rating: Int,
    val review_text: String?,
    val visit_weather: VisitWeather
)

data class UserInteraction(
    val id: Int,
    val created_at: String,
    val users_id: Int,
    val culinary_places_id: Int,
    val interaction_type: InteractionType,
    val interaction_value: Double
)

data class UserInteractionRequest(
    val users_id: Int,
    val culinary_places_id: Int,
    val interaction_type: InteractionType,
    val interaction_value: Double
)

data class UserPreference(
    val id: Int,
    val created_at: String,
    val users_id: Int,
    val preferred_category: String?,
    val preferred_price_range: String?,
    val preferred_weather: String?,
    val updated_at: String?
)

data class UserPreferenceRequest(
    val users_id: Int,
    val preferred_category: String?,
    val preferred_price_range: String?,
    val preferred_weather: String?
)

data class User(
    val id: Int,
    val created_at: String,
    val name: String,
    val email: String,
    val avatar: String?
)

enum class VisitWeather {
    PANAS,
    DINGIN,
    KEDUANYA
}

enum class InteractionType {
    VIEW,
    CLICK,
    FAVORITE,
    REVIEW
}