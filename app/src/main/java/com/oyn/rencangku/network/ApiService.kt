package com.oyn.rencangku.network

import com.oyn.rencangku.data.AuthResponse
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.data.Favorite
import com.oyn.rencangku.data.FavoriteRequest
import com.oyn.rencangku.data.LoginRequest
import com.oyn.rencangku.data.LoginResponse
import com.oyn.rencangku.data.Review
import com.oyn.rencangku.data.ReviewRequest
import com.oyn.rencangku.data.SignupRequest
import com.oyn.rencangku.data.User
import com.oyn.rencangku.data.UserInteraction
import com.oyn.rencangku.data.UserInteractionRequest
import com.oyn.rencangku.data.UserPreference
import com.oyn.rencangku.data.UserPreferenceRequest
import retrofit2.http.GET

import retrofit2.http.*

interface ApiService {

    /* =========================
       CULINARY PLACES
       ========================= */
    @GET("culinary_places")
    suspend fun getCulinaryPlaces(): List<CulinaryPlace>

    @GET("culinary_places/{id}")
    suspend fun getCulinaryPlaceDetail(
        @Path("id") id: Int
    ): CulinaryPlace


    /* =========================
       FAVORITES
       ========================= */
    @GET("favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): List<Favorite>

    @GET("favorites/{id}")
    suspend fun getFavoriteDetail(
        @Path("id") id: Int
    ): Favorite

    @POST("favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoriteRequest
    ): Favorite

    @PATCH("favorites/{id}")
    suspend fun updateFavorite(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: FavoriteRequest
    ): Favorite

    @DELETE("favorites/{id}")
    suspend fun deleteFavorite(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    /* =========================
       REVIEWS
       ========================= */
    @GET("reviews")
    suspend fun getReviews(): List<Review>

    @GET("reviews/{id}")
    suspend fun getReviewDetail(
        @Path("id") id: Int
    ): Review

    @POST("reviews")
    suspend fun addReview(
        @Body request: ReviewRequest
    ): Review

    @PATCH("reviews/{id}")
    suspend fun updateReview(
        @Path("id") id: Int,
        @Body request: ReviewRequest
    ): Review

    @DELETE("reviews/{id}")
    suspend fun deleteReview(
        @Path("id") id: Int
    )


    /* =========================
       USER INTERACTIONS
       ========================= */
    @GET("user_interactions")
    suspend fun getUserInteractions(): List<UserInteraction>

    @GET("user_interactions/{id}")
    suspend fun getUserInteractionDetail(
        @Path("id") id: Int
    ): UserInteraction

    @POST("user_interactions")
    suspend fun addUserInteraction(
        @Body request: UserInteractionRequest
    ): UserInteraction


    /* =========================
       USER PREFERENCES
       ========================= */
    @GET("user_preferences")
    suspend fun getUserPreferences(): List<UserPreference>

    @GET("user_preferences/{id}")
    suspend fun getUserPreferenceDetail(
        @Path("id") id: Int
    ): UserPreference

    @POST("user_preferences")
    suspend fun addUserPreference(
        @Body request: UserPreferenceRequest
    ): UserPreference

    @PATCH("user_preferences/{id}")
    suspend fun updateUserPreference(
        @Path("id") id: Int,
        @Body request: UserPreferenceRequest
    ): UserPreference

    /* =========================
        USERS
       ========================= */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): AuthResponse

    @GET("auth/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): User
}
