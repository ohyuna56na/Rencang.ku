package com.oyn.rencangku.ui.detailResto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.data.FavoriteRequest
import com.oyn.rencangku.data.Review
import com.oyn.rencangku.data.ReviewRequest
import com.oyn.rencangku.databinding.ActivityDetailRestoBinding
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.home.CulinaryAdapter
import com.oyn.rencangku.ui.home.HomeViewModel
import com.oyn.rencangku.ui.home.HomeViewModelFactory
import com.oyn.rencangku.ui.maps.MapsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID

@Suppress("DEPRECATION")
class DetailRestoActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var relatedAdapter: CulinaryAdapter
    private lateinit var binding: ActivityDetailRestoBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var placesClient: PlacesClient
    private var selectedRestaurant: CulinaryPlace? = null
    private var isFavorite = false
    private var reviewList = mutableListOf<Review>()
    private var selectedImageUri: Uri? = null
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                selectedImageUri = uri
                selectedPreviewImage?.setImageURI(uri)
            }
        }
    private var selectedPreviewImage: ImageView? = null
    private val TAG = "DetailRestoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRestoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val factory = HomeViewModelFactory(
            ApiClient.RestaurantApi,
            sessionManager
        )

        viewModel = ViewModelProvider(
            this,
            factory
        )[HomeViewModel::class.java]

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

        relatedAdapter = CulinaryAdapter { restaurant ->

            val intent = Intent(
                this,
                DetailRestoActivity::class.java
            )

            intent.putExtra(
                "SELECTED_RESTAURANT",
                restaurant
            )

            startActivity(intent)
        }

        bindRestaurantData(selectedRestaurant!!)

        binding.itemKategori.apply {

            layoutManager = LinearLayoutManager(
                this@DetailRestoActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = relatedAdapter
        }

        viewModel.relatedRestaurant.observe(this) { list ->

            binding.progressRelated.visibility = View.GONE

            if (list.isEmpty()) {
                binding.itemKategori.visibility = View.GONE
                binding.tvEmptyRelated.visibility = View.VISIBLE
            } else {
                binding.itemKategori.visibility = View.VISIBLE
                binding.tvEmptyRelated.visibility = View.GONE
                relatedAdapter.submitList(list)
            }
        }

        applySafeArea()
        checkIsFavoriteFromAPI()

        binding.progressRelated.visibility = View.VISIBLE
        binding.itemKategori.visibility = View.GONE

        viewModel.loadRelatedRestaurant(
            selectedRestaurant!!.displayCategory,
            selectedRestaurant!!.id!!
        )

        binding.backButtonDetail.setOnClickListener { finish() }
        binding.ButtonCekLokasi.setOnClickListener { openMaps(selectedRestaurant!!) }
        binding.imgFavorite.setOnClickListener { toggleFavorite(selectedRestaurant!!) }
        binding.btnAddReview.setOnClickListener {

            val myReview = reviewList.firstOrNull {
                it.users_id == sessionManager.getUserId()
            }

            if (myReview != null) {
                showReviewDialog(myReview)
            } else {
                showReviewDialog(null)
            }
        }
        loadReviews()
    }

    private fun applySafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.backButton) { v, insets ->
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

    private fun loadReviews() {
        binding.progressReview.visibility = View.VISIBLE
        binding.itemFeedbackDetail.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val apiKey = ApiClient.API_KEY
                val auth = "Bearer $apiKey"

                val restaurantId = selectedRestaurant?.id ?: return@launch

                val reviews = ApiClient.RestaurantApi.getReviews(
                    apiKey,
                    auth,
                    "eq.$restaurantId"
                )

                reviewList.clear()
                reviewList.addAll(reviews)
                if (reviewList.isEmpty()) {
                    binding.itemFeedbackDetail.visibility = View.GONE
                    binding.tvEmptyReview.visibility = View.VISIBLE
                } else {
                    binding.itemFeedbackDetail.visibility = View.VISIBLE
                    binding.tvEmptyReview.visibility = View.GONE
                }
                binding.progressReview.visibility = View.GONE
                binding.itemFeedbackDetail.visibility = View.VISIBLE

                Log.d("REVIEW", "Jumlah review = ${reviewList.size}")

                reviewList.forEach {
                    Log.d(
                        "REVIEW",
                        "id=${it.id} text=${it.review_text} photo=${it.photos}"
                    )
                }

                if (binding.itemFeedbackDetail.adapter == null) {
                    binding.itemFeedbackDetail.layoutManager =
                        LinearLayoutManager(this@DetailRestoActivity)

                    binding.itemFeedbackDetail.adapter =
                        FeedbackAdapter(
                            reviewList,
                            sessionManager,
                            onEditClick = { editReview(it) },
                            onDeleteClick = { deleteReview(it) }
                        )
                } else {
                    (binding.itemFeedbackDetail.adapter as FeedbackAdapter)
                        .updateData(reviewList)
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    private fun deleteReview(review: Review) {
        lifecycleScope.launch {
            try {
                val apiKey = ApiClient.API_KEY
                val auth = "Bearer $apiKey"

                ApiClient.RestaurantApi.deleteReview(
                    apiKey,
                    auth,
                    "eq.${review.id}"
                )

                loadReviews()
                reviewList.removeAll { it.id == review.id }
                updateAdapter()

                Toast.makeText(
                    this@DetailRestoActivity,
                    "Review dihapus",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("DELETE_REVIEW", Log.getStackTraceString(e))
                Toast.makeText(
                    this@DetailRestoActivity,
                    Log.getStackTraceString(e),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun editReview(review: Review) {
        showReviewDialog(review)
    }

    private fun showReviewDialog(review: Review?) {

        val view = layoutInflater.inflate(R.layout.dialog_review, null)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBarReview)
        val etReview = view.findViewById<EditText>(R.id.etReview)
        val imgPreview = view.findViewById<ImageView>(R.id.imgPreview)
        val btnChoosePhoto = view.findViewById<Button>(R.id.btnChoosePhoto)

        btnChoosePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        selectedPreviewImage = imgPreview

        if (review != null) {
            ratingBar.rating = review.rating.toFloat()
            etReview.setText(review.review_text)
        }

        AlertDialog.Builder(this)
            .setTitle(
                if (review == null)
                    "Tambah Review"
                else
                    "Edit Review"
            )
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                Log.d("PHOTO", "URI = $selectedImageUri")
                val rating = ratingBar.rating.toInt()
                val text = etReview.text.toString()

                if (review == null) {
                    addReview(rating, text)
                } else {
                    updateReview(review.id, rating, text)
                }

            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun addReview(
        rating: Int,
        reviewText: String
    ) {
        lifecycleScope.launch {

            try {
                Log.d("PHOTO", "URI = $selectedImageUri")

                val imageUrl = uploadImage()

                Log.d("UPLOAD", "imageUrl = $imageUrl")
                val response = ApiClient.RestaurantApi.addReview(
                    ApiClient.API_KEY,
                    "Bearer ${ApiClient.API_KEY}",
                    "eq.${sessionManager.getUserId()}",
                    ReviewRequest(
                        users_id = sessionManager.getUserId(),
                        culinary_places_id = selectedRestaurant!!.id!!,
                        rating = rating,
                        review_text = reviewText,
                        photos = imageUrl
                    )
                )

                if (response.isSuccessful) {
                    loadReviews()
                    selectedImageUri = null

                    Toast.makeText(
                        this@DetailRestoActivity,
                        "Review berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("ADD_REVIEW", Log.getStackTraceString(e))
                Toast.makeText(
                    this@DetailRestoActivity,
                    Log.getStackTraceString(e),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }
    }

    private fun updateReview(
        reviewId: Int,
        rating: Int,
        reviewText: String
    ) {

        lifecycleScope.launch {

            try {
                Log.d("PHOTO", "URI = $selectedImageUri")

                val imageUrl = uploadImage()
                val response = ApiClient.RestaurantApi.updateReview(
                    apiKey = ApiClient.API_KEY,
                    auth = "Bearer ${ApiClient.API_KEY}",
                    id = "eq.$reviewId",
                    request = ReviewRequest(
                        users_id = sessionManager.getUserId(),
                        culinary_places_id = selectedRestaurant!!.id!!,
                        rating = rating,
                        review_text = reviewText,
                        photos = imageUrl
                    )
                )

                if (response.isSuccessful) {
                    loadReviews()
                    selectedImageUri = null

                    Toast.makeText(
                        this@DetailRestoActivity,
                        "Review berhasil diupdate",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Log.e("UPDATE_REVIEW", "Gagal update review: ${response.code()} - ${response.message()}")
                    Toast.makeText(
                        this@DetailRestoActivity,
                        "Gagal update review",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("UPDATE_REVIEW", Log.getStackTraceString(e))
                Toast.makeText(
                    this@DetailRestoActivity,
                    Log.getStackTraceString(e),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

    private fun updateAdapter() {
        (binding.itemFeedbackDetail.adapter as FeedbackAdapter)
            .updateData(reviewList)
    }

    private suspend fun uploadImage(): String? = withContext(Dispatchers.IO) {

        val uri = selectedImageUri ?: return@withContext null

        val input = contentResolver.openInputStream(uri) ?: return@withContext null

        val file = File(cacheDir, "${UUID.randomUUID()}.jpg")

        file.outputStream().use {
            input.copyTo(it)
        }

        val body = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val path = "${UUID.randomUUID()}.jpg"

        val request = Request.Builder()
            .url("${ApiClient.STORAGE_URL}/${ApiClient.BUCKET}/$path")
            .addHeader("apikey", ApiClient.API_KEY)
            .addHeader("Authorization", "Bearer ${ApiClient.API_KEY}")
            .post(body)
            .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            Log.e("UPLOAD", "Code=${response.code}")
            Log.e("UPLOAD", response.body?.string().orEmpty())
            return@withContext null
        }

        "https://xrchjsinkfbgnhweyign.supabase.co/storage/v1/object/public/${ApiClient.BUCKET}/$path"
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