package com.oyn.rencangku.network

import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.data.Favorite
import com.oyn.rencangku.data.FavoriteRequest
import com.oyn.rencangku.data.Review
import com.oyn.rencangku.data.ReviewRequest
import com.oyn.rencangku.data.SignupRequest
import com.oyn.rencangku.data.User
import com.oyn.rencangku.data.UserInteraction
import com.oyn.rencangku.data.UserInteractionRequest
import retrofit2.http.GET

import retrofit2.http.*

interface ApiService {

    /* =========================
       CULINARY PLACES
       ========================= */
    @GET("culinary_places")
    suspend fun getCulinaryPlaces(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String
    ): List<CulinaryPlace>

    @GET("culinary_places/{id}")
    suspend fun getCulinaryPlaceDetail(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Path("id") id: Int
    ): CulinaryPlace


    /* =========================
       FAVORITES
       ========================= */
    @GET("favorites?select=*,culinary_places(*)")
    suspend fun getFavorites(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String // tetap
    ): List<Favorite>

    @GET("favorites/{id}")
    suspend fun getFavoriteDetail(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Path("id") id: Int
    ): Favorite

    @POST("favorites")
    suspend fun addFavorite(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body request: FavoriteRequest
    ): List<Favorite>

    @PATCH("favorites/{id}")
    suspend fun updateFavorite(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Path("id") id: Int,
        @Body request: FavoriteRequest
    ): Favorite

    @DELETE("favorites")
    suspend fun deleteFavorite(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Query("id") id: String
    )

    /* =========================
       REVIEWS
       ========================= */
    @GET("reviews")
    suspend fun getReviews(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String
    ): List<Review>

    @GET("reviews/{id}")
    suspend fun getReviewDetail(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Path("id") id: Int
    ): Review

    @POST("reviews")
    suspend fun addReview(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Body request: ReviewRequest
    ): Review

    @PATCH("reviews/{id}")
    suspend fun updateReview(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Path("id") id: Int,
        @Body request: ReviewRequest
    ): Review

    @DELETE("reviews/{id}")
    suspend fun deleteReview(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String,
        @Path("id") id: Int
    )


    /* =========================
       USER INTERACTIONS
       ========================= */
    @GET("user_interactions")
    suspend fun getUserInteractions(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("users_id") userId: String
    ): List<UserInteraction>

    @GET("user_interactions/{id}")
    suspend fun getUserInteractionDetail(
        @Header("apikey") apiKey: String,

        @Query("users_id") userId: String,
        @Path("id") id: Int
    ): UserInteraction

    @POST("user_interactions")
    suspend fun addUserInteraction(
        @Header("apikey") apiKey: String,
        @Query("users_id") userId: String,
        @Body request: UserInteractionRequest
    ): UserInteraction

    /* =========================
        USERS
       ========================= */
    @GET("users")
    suspend fun login(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): List<User>

    @POST("users")
    suspend fun signup(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body request: SignupRequest
    ): List<User>

    @GET("users")
    suspend fun getProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("id") id: String,
        @Query("select") select: String = "*"
    ): List<User>

    @GET("users")
    suspend fun checkEmail(

        @Header("apikey") apiKey: String,

        @Header("Authorization") auth: String,

        @Query("email") email: String,

        @Query("select") select: String = "*"

    ): List<User>
}
