package com.oyn.rencangku.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.FragmentHomeBinding
import com.oyn.rencangku.network.ApiClient
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: CulinaryAdapter

    companion object {
        private const val TAG = "HomeFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        val sessionManager = SessionManager(requireContext())
        val factory = HomeViewModelFactory(
            ApiClient.RestaurantApi,
            sessionManager
        )

        viewModel = ViewModelProvider(this, factory)
            .get(HomeViewModel::class.java)

        Log.d(TAG, "onViewCreated")

        applySafeArea()
        setupRecyclerView()
        observeViewModel()
        loadData()
    }

    private fun applySafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.nested) { v, insets ->
            val topInset =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, topInset, 0, 0)
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = CulinaryAdapter()
        binding.itemRestoran.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@HomeFragment.adapter
        }
    }

    private fun observeViewModel() {

        viewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            Log.d(TAG, "Weather update: $weather")
            binding.TvWeather.text =
                weather?.main?.temp?.toInt()?.let { "$it°C" }
                    ?: "Cuaca tidak tersedia"
        }

        viewModel.locationName.observe(viewLifecycleOwner) { city ->
            Log.d(TAG, "Location: $city")
            binding.TvLocation.text = city
        }

        viewModel.culinaryPlaces.observe(viewLifecycleOwner) { list ->
            Log.d(TAG, "Culinary data size: ${list.size}")

            adapter.submitList(list)

            val loading = viewModel.isLoading.value ?: false

            binding.tvNoResults.visibility =
                if (!loading && list.isEmpty())
                    View.VISIBLE
                else
                    View.GONE

        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d(TAG, "Loading state: $isLoading")

            binding.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE

            if (isLoading) {
                binding.tvNoResults.visibility = View.GONE
            }
        }
        viewModel.getUsername()
    }

    private fun loadData() {
        Log.d(TAG, "loadData() called")
        observeData()
        requestLocationPermission()
        viewModel.loadCulinaryPlaces()
    }


    private fun observeData() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                    in 5..10 -> "Selamat Pagi"
                    in 11..14 -> "Selamat Siang"
                    in 15..18 -> "Selamat Sore"
                    else -> "Selamat Malam"
                }
                binding.TvNameUsers.text = "$greeting,\n${user.name}!"
            } else {
                binding.TvNameUsers.text = "Hello, Guest!"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Request location permission")
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
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Log.e(TAG, "Location is NULL")
                return@addOnSuccessListener
            }

            Log.d(TAG, "Lat: ${location.latitude}, Lon: ${location.longitude}")

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val city = geocoder
                .getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                ?.firstOrNull()
                ?.locality ?: "Lokasi Tidak Diketahui"

            Log.d(TAG, "City detected: $city")

            viewModel.setLocation(city)
            viewModel.fetchWeather(
                location.latitude,
                location.longitude
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
