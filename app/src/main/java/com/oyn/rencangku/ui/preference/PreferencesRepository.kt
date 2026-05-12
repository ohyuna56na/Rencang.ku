package com.oyn.rencangku.ui.preference

import com.oyn.rencangku.data.UserPreferenceRequest
import com.oyn.rencangku.data.UserPreferenceResponse
import com.oyn.rencangku.network.ApiClient

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class PreferenceRepository {

    private val api = ApiClient.RestaurantApi

    /**
     * Cek apakah user sudah punya preference.
     * Supabase filter pakai format "eq.<value>" bukan "=<value>".
     * Return: null jika belum ada, UserPreferenceResponse jika sudah ada.
     */
    suspend fun getUserPreference(userId: Int): Result<UserPreferenceResponse?> {
        return try {
            val response = api.checkUserPreference(
                userId = "eq.$userId"
            )
            if (response.isSuccessful) {
                val list = response.body()
                if (list.isNullOrEmpty()) {
                    Result.Success(null)         // belum ada preference
                } else {
                    Result.Success(list.first()) // sudah ada
                }
            } else {
                Result.Error("Gagal memeriksa preferensi: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan jaringan")
        }
    }

    /**
     * Simpan preference baru.
     * Supabase POST mengembalikan List, ambil elemen pertama.
     */
    suspend fun savePreference(request: UserPreferenceRequest): Result<UserPreferenceResponse> {
        return try {
            val response = api.saveUserPreference(request = request)
            if (response.isSuccessful) {
                val body = response.body()
                if (!body.isNullOrEmpty()) {
                    Result.Success(body.first())
                } else {
                    // Supabase kadang 201 tanpa body jika Prefer header tidak diset
                    Result.Success(
                        UserPreferenceResponse(
                            id = 0, createdAt = "",
                            usersId = request.usersId,
                            favoriteCategory = request.favoriteCategory,
                            favoritePrice = request.favoritePrice,
                            favoriteRating = request.favoriteRating,
                            favoriteOpenTime = request.favoriteOpenTime,
                            favoriteWeather = request.favoriteWeather
                        )
                    )
                }
            } else {
                Result.Error("Gagal menyimpan preferensi: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan jaringan")
        }
    }

    /**
     * Update preference yang sudah ada (filter by users_id).
     */
    suspend fun updatePreference(
        userId: Int,
        request: UserPreferenceRequest
    ): Result<UserPreferenceResponse> {
        return try {
            val response = api.updateUserPreference(
                userId  = "eq.$userId",
                request = request
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (!body.isNullOrEmpty()) {
                    Result.Success(body.first())
                } else {
                    Result.Success(
                        UserPreferenceResponse(
                            id = 0, createdAt = "",
                            usersId = request.usersId,
                            favoriteCategory = request.favoriteCategory,
                            favoritePrice = request.favoritePrice,
                            favoriteRating = request.favoriteRating,
                            favoriteOpenTime = request.favoriteOpenTime,
                            favoriteWeather = request.favoriteWeather
                        )
                    )
                }
            } else {
                Result.Error("Gagal memperbarui preferensi: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan jaringan")
        }
    }
}