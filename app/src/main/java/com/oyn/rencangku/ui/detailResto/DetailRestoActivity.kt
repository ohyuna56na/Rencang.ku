package com.oyn.rencangku.ui.detailResto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.data.FavoriteRequest
import com.oyn.rencangku.databinding.ActivityDetailRestoBinding
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.maps.MapsActivity
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class DetailRestoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRestoBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var placesClient: PlacesClient
    private var selectedRestaurant: CulinaryPlace? = null
    private var isFavorite = false
    private val TAG = "DetailRestoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRestoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        try {
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, "AIzaSyC5z7pB1rtnC_cgB0ErIbi8pwk0y2b9zbY")
            }
            placesClient = Places.createClient(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Places API: ${e.message}")
        }

        // ── Ambil CulinaryPlace dari Intent ──────────────────────
        // CulinaryPlace sudah @Parcelize, jadi bisa langsung getParcelableExtra
        selectedRestaurant = intent.getParcelableExtra<CulinaryPlace>("SELECTED_RESTAURANT")

        if (selectedRestaurant == null) {
            Toast.makeText(this, "Data restoran tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bindRestaurantData(selectedRestaurant!!)
        applySafeArea()
        checkIsFavoriteFromAPI()

        binding.backButtonDetail.setOnClickListener { finish() }
        binding.ButtonCekLokasi.setOnClickListener { openMaps(selectedRestaurant!!) }
        binding.imgFavorite.setOnClickListener { toggleFavorite(selectedRestaurant!!) }
    }

    private fun applySafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.nested) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, top, 0, 0)
            insets
        }
    }

    private fun bindRestaurantData(restaurant: CulinaryPlace) {
        // Pakai display properties — otomatis resolve dari ML API atau Supabase
        binding.tvRestaurantDetail.text   = restaurant.displayTitle
        binding.tvDetailAddress.text      = restaurant.displayAddress.ifEmpty { "Alamat tidak tersedia" }
        binding.tvPhone.text              = restaurant.phone ?: "Nomor telepon tidak tersedia"
        binding.tvPriceRange.text         = restaurant.displayPriceRange.ifEmpty { "Harga tidak tersedia" }
        binding.tvCategoriSuhuDetail.text = restaurant.displayWeather.ifEmpty { "Informasi cuaca tidak tersedia" }
        binding.tvratingsDetail.text      =
            if (restaurant.displayRating > 0)
                "${restaurant.displayRating} (${restaurant.displayRatingCount})"
            else "0"

        setupOperationalHours(restaurant.openHours)
    }

    private fun setupOperationalHours(openHours: Map<String, List<String>>?) {
        if (openHours == null) return

        val orderedDays = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        val list = orderedDays.map { day ->
            val time = openHours[day]?.firstOrNull() ?: "Tutup"
            day to time
        }

        binding.rvOperationalHours.apply {
            layoutManager = LinearLayoutManager(this@DetailRestoActivity)
            adapter = OperationalHoursAdapter(list)
        }
    }

    private fun openMaps(restaurant: CulinaryPlace) {
        val lat = restaurant.displayLatitude
        val lng = restaurant.displayLongitude

        if (lat == null || lng == null) {
            Toast.makeText(this, "Koordinat tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra("LATITUDE", lat)
            putExtra("LONGITUDE", lng)
            putExtra("RESTAURANT_NAME", restaurant.displayTitle)
            putExtra("RESTAURANT_ADDRESS", restaurant.displayAddress)
            putExtra("CATEGORY_ID", restaurant.displayCategory)
            putExtra("RESTAURANT_WEATHER", restaurant.displayWeather)
            putExtra("RESTAURANT_RATING", restaurant.displayRating)
        }
        startActivity(intent)
    }

    private fun toggleFavorite(data: CulinaryPlace) {
        lifecycleScope.launch {
            try {
                val apiKey = ApiClient.API_KEY
                val auth   = "Bearer $apiKey"
                val userId = sessionManager.getUserId()

                if (userId == -1) {
                    Toast.makeText(this@DetailRestoActivity, "User belum login", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // id tempat — wajib ada, kalau null skip
                val placeId = data.id ?: run {
                    Toast.makeText(this@DetailRestoActivity, "ID tempat tidak valid", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (!isFavorite) {
                    val result = ApiClient.RestaurantApi.addFavorite(
                        apiKey  = apiKey,
                        auth    = auth,
                        request = FavoriteRequest(
                            users_id            = userId,
                            culinary_places_id  = placeId
                        )
                    )
                    if (result.isNotEmpty()) {
                        isFavorite = true
                        Toast.makeText(this@DetailRestoActivity, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    val favorites = ApiClient.RestaurantApi.getFavorites(
                        apiKey = apiKey,
                        auth   = auth,
                        userId = "eq.$userId"
                    )
                    val fav = favorites.firstOrNull { it.culinary_places_id == placeId }

                    if (fav != null) {
                        ApiClient.RestaurantApi.deleteFavorite(
                            apiKey = apiKey,
                            auth   = auth,
                            userId = "eq.$userId",
                            id     = "eq.${fav.id}"
                        )
                        isFavorite = false
                        Toast.makeText(this@DetailRestoActivity, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DetailRestoActivity, "Data favorite tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                updateFavoriteIcon()

            } catch (e: Exception) {
                Log.e(TAG, "toggleFavorite error: ${e.message}")
                Toast.makeText(this@DetailRestoActivity, "Gagal update favorite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIsFavoriteFromAPI() {
        lifecycleScope.launch {
            try {
                val apiKey = ApiClient.API_KEY
                val auth   = "Bearer $apiKey"
                val userId = sessionManager.getUserId().toString()

                val favorites = ApiClient.RestaurantApi.getFavorites(
                    apiKey = apiKey,
                    auth   = auth,
                    userId = "eq.$userId"
                )
                isFavorite = favorites.any { it.culinary_places_id == selectedRestaurant?.id }
                updateFavoriteIcon()
            } catch (_: Exception) {}
        }
    }

    private fun updateFavoriteIcon() {
        binding.imgFavorite.setImageResource(
            if (isFavorite) R.drawable.favorite_bold else R.drawable.favorite_line
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            (placesClient as? AutoCloseable)?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down PlacesClient: ${e.message}")
        }
    }
}