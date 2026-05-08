package com.oyn.rencangku.ui.maps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.ActivityMapsBinding
import com.oyn.rencangku.ui.detailResto.DetailRestoActivity
import com.oyn.rencangku.ui.home.CulinaryAdapter
import com.oyn.rencangku.ui.home.HomeRepository
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var culinaryAdapter: CulinaryAdapter
    private lateinit var repository: HomeRepository
    private lateinit var sessionManager: SessionManager
    private var latitude = 0.0
    private var longitude = 0.0
    private var restaurantName = ""
    private var restaurantAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(this)
        repository = HomeRepository(sessionManager)

        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonMaps.setOnClickListener { finish() }

        binding.ButtonCekLokasi.setOnClickListener {
            val intent = Intent(this, MapsNavigationActivity::class.java)
            intent.putExtra("RESTAURANT_LOCATION", LatLng(latitude, longitude))
            intent.putExtra("RESTAURANT_NAME", restaurantName)
            intent.putExtra("RESTAURANT_ADDRESS", restaurantAddress)
            startActivity(intent)
        }

        getIntentData()
        setupMap()
        setupRecyclerView()
        loadRestaurants()
    }

    private fun getIntentData() {
        latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
        restaurantName = intent.getStringExtra("RESTAURANT_NAME") ?: "Lokasi Restoran"

        binding.tvRestaurantMaps.text = restaurantName
        binding.tvMapsAddress.text =
            intent.getStringExtra("RESTAURANT_ADDRESS") ?: "-"
        binding.tvCategoriSuhuMaps.text =
            intent.getStringExtra("RESTAURANT_WEATHER") ?: "-"
        binding.tvratingsMaps.text =
            intent.getStringExtra("RESTAURANT_RATING") ?: "0.0"
    }

    private fun setupMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(
                com.oyn.rencangku.R.id.map
            ) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val location = LatLng(latitude, longitude)

        mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(restaurantName)
        )

        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(location, 15f)
        )

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
    }

    private fun setupRecyclerView() {
        culinaryAdapter = CulinaryAdapter { restaurant ->
            val intent = Intent(this, DetailRestoActivity::class.java)
            intent.putExtra("SELECTED_RESTAURANT", restaurant)
            startActivity(intent)
        }

        binding.itemRestoran.apply {
            layoutManager = GridLayoutManager(this@MapsActivity, 2)
            adapter = culinaryAdapter
        }
    }

    private fun loadRestaurants() {
        lifecycleScope.launch {
            try {
                val data = repository.getCulinaryPlaces()
                culinaryAdapter.submitList(data.take(4))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}