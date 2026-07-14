package com.oyn.rencangku.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.oyn.rencangku.R
import androidx.appcompat.widget.SearchView
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.FragmentHomeBinding
import com.oyn.rencangku.ml.MLApiClient
import com.oyn.rencangku.ml.RecommendRequest
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.detailResto.DetailRestoActivity
import com.oyn.rencangku.ui.home.category.CategoryAdapter
import com.oyn.rencangku.ui.preference.PreferenceActivity
import com.oyn.rencangku.ui.preference.PreferenceRepository
import com.oyn.rencangku.ui.preference.Result
import com.oyn.rencangku.ui.search.SearchActivity
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: CulinaryAdapter
    private lateinit var sessionManager: SessionManager

    // Repository preference — hanya untuk cek sekali di Home
    private val preferenceRepository by lazy { PreferenceRepository() }

    // Adapter category
    private lateinit var categoryAdapter: CategoryAdapter

    companion object {
        private const val TAG = "HomeFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        val factory = HomeViewModelFactory(ApiClient.RestaurantApi, sessionManager)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        applySafeArea()
        setupRecyclerView()
        setupCategoryRecycler()
        observeViewModel()

        viewModel.loadCulinaryPlaces()
        loadData()

        binding.ivSearch.setOnClickListener {

            startActivity(
                Intent(requireContext(), SearchActivity::class.java)
            )

        }

        binding.TvAllResto.setOnClickListener {
            startActivity(
                Intent(requireContext(), AllRestaurantActivity::class.java)
            )
        }

        // Cek preference saat HomeFragment pertama kali dibuka
        checkUserPreference()
    }

    /**
     * Cek apakah user sudah punya preference.
     * Jika belum → tampilkan dialog popup.
     * Dipanggil 1x saat HomeFragment onViewCreated.
     */
    private fun checkUserPreference() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return  // belum login, skip

        lifecycleScope.launch {
            when (val result = preferenceRepository.getUserPreference(userId)) {
                is Result.Success -> {
                    if (result.data == null) {
                        // Belum ada preference → tampilkan dialog
                        Log.d(TAG, "User $userId belum punya preference")
                        showNoPreferenceDialog()
                    } else {
                        Log.d(TAG, "User $userId sudah punya preference")
                        // Lanjut normal, tidak perlu apa-apa
                    }
                }
                is Result.Error -> {
                    // Gagal cek → tidak tampilkan dialog, biarkan user lanjut
                    Log.e(TAG, "Gagal cek preference: ${result.message}")
                }
                is Result.Loading -> { /* tidak akan sampai sini */ }
            }
        }
    }

    /**
     * Dialog popup yang muncul ketika user belum mengisi preference.
     * Tombol "Atur Sekarang" → ke PreferenceActivity
     * Tombol "Nanti Saja"   → tutup dialog, lanjut ke home
     */
    private fun showNoPreferenceDialog() {
        if (!isAdded || activity == null) return

        AlertDialog.Builder(requireContext(), R.style.PreferenceDialogTheme)
            .setTitle("Preferensi Belum Diatur")
            .setMessage(
                "Kamu belum memiliki preferensi kuliner. " +
                        "Atur preferensimu agar rekomendasi tempat makan " +
                        "lebih sesuai dengan seleramu! 🍜"
            )
            .setPositiveButton("Atur Sekarang") { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(requireContext(), PreferenceActivity::class.java))
            }
            .setNegativeButton("Nanti Saja") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)  // user harus pilih salah satu tombol
            .show()
    }

    private fun applySafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.nested) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, top, 0, 0)
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = CulinaryAdapter { culinaryPlace ->
            val intent = Intent(requireContext(), DetailRestoActivity::class.java)
            intent.putExtra("SELECTED_RESTAURANT", culinaryPlace)
            startActivity(intent)
        }
        binding.itemRestoran.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false
            adapter = this@HomeFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            binding.TvWeather.text =
                weather?.main?.temp?.toInt()?.let { "$it°C" } ?: "Cuaca tidak tersedia"
        }

        viewModel.locationName.observe(viewLifecycleOwner) { city ->
            binding.TvLocation.text = city
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                    in 5..10  -> "Selamat Pagi"
                    in 11..14 -> "Selamat Siang"
                    in 15..18 -> "Selamat Sore"
                    else      -> "Selamat Malam"
                }
                binding.TvNameUsers.text = "$greeting,\n${user.name}!"
            } else {
                binding.TvNameUsers.text = "Hello, Guest!"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.getUsername()

        viewModel.categories.observe(viewLifecycleOwner) { list ->
            categoryAdapter.submitList(list)
        }
    }

    private fun loadData() {
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fetchCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) return@addOnSuccessListener
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val city = geocoder
                .getFromLocation(location.latitude, location.longitude, 1)
                ?.firstOrNull()?.locality ?: "Lokasi Tidak Diketahui"
            viewModel.setLocation(city)
            viewModel.fetchWeather(location.latitude, location.longitude)

            loadRecommendations()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation(onResult: (Double, Double) -> Unit) {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            } else {
                Toast.makeText(requireContext(), "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCategoryRecycler() {

        categoryAdapter = CategoryAdapter { category ->

            val intent = Intent(
                requireContext(),
                AllRestaurantActivity::class.java
            )

            intent.putExtra("CATEGORY", category)

            startActivity(intent)
        }

        binding.rvCategory.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvCategory.adapter = categoryAdapter
    }

    private fun loadRecommendations() {
        val userId = sessionManager.getUserId()

        binding.progressBar.visibility = View.VISIBLE
        binding.itemRestoran.visibility = View.INVISIBLE
        binding.tvNoResults.visibility = View.GONE

        getUserLocation { lat, lon ->
            lifecycleScope.launch {
                try {
                    val response = MLApiClient.api.getRecommendations(
                        RecommendRequest(
                            userId = userId,
                            latitude = lat,
                            longitude = lon,
                            topN = 10,
                            autoWeather = true
                        )
                    )

                    binding.progressBar.visibility = View.GONE
                    binding.itemRestoran.visibility = View.VISIBLE

                    adapter.submitList(response.recommendations)

                    if (response.recommendations.isNotEmpty()) {
                        adapter.submitList(response.recommendations)
                    } else {
                        binding.tvNoResults.visibility = View.VISIBLE
                    }

                    Log.d(
                        TAG,
                        "ML loaded: ${response.recommendations.size}, mode=${response.mode}"
                    )

                } catch (e: Exception) {
                    binding.progressBar.visibility = View.GONE
                    binding.itemRestoran.visibility = View.GONE
                    binding.tvNoResults.visibility = View.VISIBLE

                    Log.e(TAG, "loadRecommendations", e)
                    Toast.makeText(
                        requireContext(),
                        "Gagal load rekomendasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}