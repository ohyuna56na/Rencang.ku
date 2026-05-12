package com.oyn.rencangku.ui.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.data.UserPreferenceRequest
import com.oyn.rencangku.data.UserPreferenceResponse
import kotlinx.coroutines.launch

class PreferenceViewModel : ViewModel() {

    private val repository = PreferenceRepository()

    // State cek preference (dipakai dari SignInActivity)
    private val _checkPreferenceState = MutableLiveData<Result<UserPreferenceResponse?>>()
    val checkPreferenceState: LiveData<Result<UserPreferenceResponse?>> = _checkPreferenceState

    // State simpan preference (dipakai dari PreferenceActivity)
    private val _savePreferenceState = MutableLiveData<Result<UserPreferenceResponse>>()
    val savePreferenceState: LiveData<Result<UserPreferenceResponse>> = _savePreferenceState

    // Pilihan user yang sedang diisi di form
    var selectedCategory: String = ""
    var selectedPrice: String    = ""
    var selectedRating: Float    = 3f
    var selectedOpenTime: String = ""
    var selectedWeather: String  = ""

    /**
     * Ubah format UI menjadi format dataset ML
     * Contoh:
     * "Bakso & Mie" -> "bakso_mie"
     * "Fast Food" -> "fast_food"
     * "Semua Cuaca" -> "semua"
     */
    private fun normalizeValue(value: String): String {
        return value
            .lowercase()
            .replace("&", "")
            .replace("–", "-")
            .replace(">", "")
            .replace("<", "")
            .replace("(", "")
            .replace(")", "")
            .replace("/", "_")
            .replace("-", "_")
            .replace("  ", " ")
            .trim()
            .replace(" ", "_")
    }

    /**
     * Cek apakah user sudah punya preference di Supabase.
     * Dipanggil dari SignInActivity setelah login berhasil.
     */
    fun checkPreference(userId: Int) {
        viewModelScope.launch {
            _checkPreferenceState.value = Result.Loading
            _checkPreferenceState.value = repository.getUserPreference(userId)
        }
    }

    /**
     * Simpan preference baru ke Supabase.
     * Dipanggil dari PreferenceActivity di step terakhir.
     */
    fun savePreference(userId: Int) {
        if (!isFormValid()) {
            _savePreferenceState.value = Result.Error("Harap lengkapi semua preferensi")
            return
        }

        val request = UserPreferenceRequest(
            usersId = userId,

            // category
            favoriteCategory = when (selectedCategory) {
                "Bakso & Mie"      -> "bakso_mie"
                "Fast Food"        -> "fast_food"
                "Seafood"          -> "seafood"
                "Steak"            -> "steak"
                "Korean"           -> "korea"
                "Pizza"            -> "pizza"
                "Chinese"          -> "chinese"
                "Cafe"             -> "cafe"
                "Healthy"          -> "healthy"
                "Bakery & Dessert" -> "bakery_dessert"
                "Indonesia"        -> "indonesia"
                "Japanese"         -> "japanese"
                "Chicken"          -> "chicken"
                "Satay"            -> "satay"
                "Martabak"         -> "martabak"
                "Food Court"       -> "food_court"
                "Other"            -> "other"
                else               -> normalizeValue(selectedCategory)
            },

            // price
            favoritePrice = when (selectedPrice) {
                "Murah (< 25rb)"     -> "murah"
                "Sedang (25–50rb)"   -> "sedang"
                "Mahal (50–100rb)"   -> "mahal"
                "Premium (> 100rb)"  -> "premium"
                else                 -> normalizeValue(selectedPrice)
            },

            favoriteRating = selectedRating,

            // open time
            favoriteOpenTime = when (selectedOpenTime) {
                "Pagi"   -> "pagi"
                "Siang"  -> "siang"
                "Malam"  -> "malam"
                "24 Jam" -> "24 jam"
                else     -> normalizeValue(selectedOpenTime)
            },

            // weather
            favoriteWeather = when (selectedWeather) {
                "Dingin"       -> "dingin"
                "Panas"        -> "panas"
                "Semua Cuaca"  -> "semua"
                else           -> normalizeValue(selectedWeather)
            }
        )

        viewModelScope.launch {
            _savePreferenceState.value = Result.Loading
            _savePreferenceState.value = repository.savePreference(request)
        }
    }

    private fun isFormValid(): Boolean =
        selectedCategory.isNotBlank() &&
                selectedPrice.isNotBlank()    &&
                selectedOpenTime.isNotBlank() &&
                selectedWeather.isNotBlank()
}