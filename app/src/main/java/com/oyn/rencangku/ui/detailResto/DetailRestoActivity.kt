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
import com.oyn.rencangku.ui.maps.MapsActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.data.FavoriteRequest
import com.oyn.rencangku.databinding.ActivityDetailRestoBinding
import com.oyn.rencangku.network.ApiClient
import kotlinx.coroutines.launch
import org.json.JSONObject

@Suppress("DEPRECATION", "UNREACHABLE_CODE")
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

        Log.d(TAG, "Activity Created")
        sessionManager = SessionManager(this)
        try {
            // Inisialisasi Google Places API
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, "AIzaSyC5z7pB1rtnC_cgB0ErIbi8pwk0y2b9zbY")
            }
            placesClient = Places.createClient(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Places API: ${e.message}")
        }

        // Ambil data restoran dari Intent
        selectedRestaurant =
            intent.getParcelableExtra("SELECTED_RESTAURANT")

        if (selectedRestaurant == null) {
            Toast.makeText(this, "Data restoran tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bindRestaurantData(selectedRestaurant!!)

        // Tombol kembali
        binding.backButtonDetail.setOnClickListener { finish() }

        // Tombol cek lokasi
        binding.ButtonCekLokasi.setOnClickListener {
            openMaps(selectedRestaurant!!)
        }

        binding.imgFavorite.setOnClickListener {
            toggleFavorite(selectedRestaurant!!)
        }

        applySafeArea()
        checkIsFavoriteFromAPI()
    }

    private fun applySafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.nested) { v, insets ->
            val topInset =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, topInset, 0, 0)
            insets
        }
    }
    private fun bindRestaurantData(restaurant: CulinaryPlace) {
        binding.tvRestaurantDetail.text = restaurant.title ?: "Nama tidak tersedia"
        binding.tvDetailAddress.text = restaurant.address ?: "Alamat tidak tersedia"
        binding.tvPhone.text = restaurant.phone ?: "Nomor telepon tidak tersedia"
        binding.tvPriceRange.text = restaurant.price_range ?: "Harga tidak tersedia"
        binding.tvCategoriSuhuDetail.text = restaurant.categorize_weather ?: "Informasi cuaca tidak tersedia"
        binding.tvratingsDetail.text =
            if (!restaurant.rating.isNullOrEmpty())
                "${restaurant.rating} (${restaurant.rating_count ?: "0"})"
            else "0"

        setupOperationalHours(restaurant.open_hours)
    }

    private fun setupOperationalHours(jsonString: String?) {

        if (jsonString.isNullOrEmpty()) return

        try {
            val cleanedJson = jsonString.replace("'", "\"")
            val jsonObject = JSONObject(cleanedJson)

            val orderedDays = listOf(
                "Senin",
                "Selasa",
                "Rabu",
                "Kamis",
                "Jumat",
                "Sabtu",
                "Minggu"
            )

            val list = mutableListOf<Pair<String, String>>()

            for (day in orderedDays) {

                val array = jsonObject.optJSONArray(day)

                val time = if (array != null && array.length() > 0)
                    array.getString(0)
                else "Tutup"

                list.add(day to time)
            }

            binding.rvOperationalHours.apply {
                layoutManager = LinearLayoutManager(this@DetailRestoActivity)
                adapter = OperationalHoursAdapter(list)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openMaps(restaurant: CulinaryPlace) {

        val lat = restaurant.latitude?.toDoubleOrNull()
        val lng = restaurant.longitude?.toDoubleOrNull()

        if (lat == null || lng == null) {
            Toast.makeText(this, "Koordinat tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra("LATITUDE", lat)
            putExtra("LONGITUDE", lng)
            putExtra("RESTAURANT_NAME", restaurant.title)
            putExtra("RESTAURANT_ADDRESS", restaurant.address)
            putExtra("CATEGORY_ID", restaurant.category)
            putExtra("RESTAURANT_WEATHER", restaurant.categorize_weather)
            putExtra("RESTAURANT_RATING", restaurant.rating)
        }

        startActivity(intent)
    }

    private fun toggleFavorite(data: CulinaryPlace) {

        lifecycleScope.launch {
            try {

                val token = "Bearer ${sessionManager.getToken()}"
                val userId = sessionManager.getUserId()

                if (userId == -1) {
                    Toast.makeText(
                        this@DetailRestoActivity,
                        "User belum login",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                if (!isFavorite) {
                    ApiClient.RestaurantApi.addFavorite(
                        token,
                        FavoriteRequest(
                            users_id = userId,
                            culinary_places_id = data.id
                        )
                    )
                    isFavorite = true
                    Toast.makeText(
                        this@DetailRestoActivity,
                        "Ditambahkan ke favorit",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    val favorites =
                        ApiClient.RestaurantApi.getFavorites(token)

                    val fav = favorites.find {
                        it.culinary_places_id == data.id
                    }

                    fav?.let {
                        ApiClient.RestaurantApi.deleteFavorite(token, it.id)
                    }

                    isFavorite = false
                    Toast.makeText(
                        this@DetailRestoActivity,
                        "Dihapus dari favorit",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                updateFavoriteIcon()

            } catch (e: Exception) {
                Log.e("FAVORITE_ERROR", e.message ?: "Unknown error")
                e.printStackTrace()
                Toast.makeText(
                    this@DetailRestoActivity,
                    "Gagal update favorite",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkIsFavoriteFromAPI() {

        lifecycleScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                val favorites = ApiClient.RestaurantApi.getFavorites(token)

                isFavorite = favorites.any {
                    it.culinary_places_id == selectedRestaurant?.id
                }

                updateFavoriteIcon()

            } catch (_: Exception) {}
        }
    }
    private fun updateFavoriteIcon() {
        binding.imgFavorite.setImageResource(
            if (isFavorite)
                R.drawable.favorite_bold
            else
                R.drawable.favorite_line
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            (placesClient as? AutoCloseable)?.close()
            Log.d("DetailRestoActivity", "PlacesClient shutdown successfully")
        } catch (e: Exception) {
            Log.e("DetailRestoActivity", "Error shutting down PlacesClient: ${e.message}")
        }
    }
}