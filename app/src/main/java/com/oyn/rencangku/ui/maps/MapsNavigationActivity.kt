package com.oyn.rencangku.ui.maps

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.location.Geocoder
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import java.util.Locale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.PolylineOptions
import com.oyn.rencangku.R
import com.oyn.rencangku.databinding.ActivityMapsNavigationBinding

class MapsNavigationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsNavigationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali
        binding.backMaps.setOnClickListener { finish() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_navigation) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.ButtonMulaiLokasi.setOnClickListener {
            Log.d("NavigationFlow", "ButtonMulaiLokasi ditekan.")

            // Buat dialog loading
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_loading)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
            Log.d("NavigationFlow", "Dialog loading ditampilkan.")

            // Inisialisasi loadingAnimation dari dialog
            val loadingAnimation = dialog.findViewById<ImageView>(R.id.loading_animation)
            if (loadingAnimation == null) {
                Log.e("NavigationFlow", "loadingAnimation tidak ditemukan di layout popup_loading.")
                return@setOnClickListener
            }

            // Animasi
            loadingAnimation.animate()
                .translationX(500f)
                .setDuration(1000)
                .withEndAction {
                    Log.d("NavigationFlow", "Animasi pertama selesai. Reset posisi animasi.")
                    loadingAnimation.translationX = 0f
                    loadingAnimation.animate()
                        .translationX(500f)
                        .setDuration(1000)
                        .start()
                    Log.d("NavigationFlow", "Animasi kedua dimulai.")
                }
                .start()
            Log.d("NavigationFlow", "Animasi dimulai.")

            // Tunda dan tutup dialog, lalu navigasi
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                Log.d("NavigationFlow", "Dialog loading ditutup.")

                val restaurantLocation = intent.getParcelableExtra<LatLng>("RESTAURANT_LOCATION")
                if (restaurantLocation != null) {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${restaurantLocation.latitude},${restaurantLocation.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    if (mapIntent.resolveActivity(packageManager) != null) {
                        sendBroadcast(Intent("com.capstone.kulinerkita.ACTION_NAVIGATION_STARTED"))
                        startActivity(mapIntent)
                    } else {
                        Toast.makeText(this, "Google Maps tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Lokasi tujuan tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }, 3000)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onLocationRetrieved: (LatLng) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                onLocationRetrieved(currentLatLng)
            } ?: run {
                // Lokasi tidak tersedia, tambahkan fallback atau pemberitahuan ke user
            }
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                addresses?.get(0)?.getAddressLine(0) ?: "Alamat tidak ditemukan"
            } else {
                "Alamat tidak ditemukan"
            }
        } catch (e: Exception) {
            "Alamat tidak tersedia"
        }
    }

    private fun getScaledBitmap(resourceId: Int, width: Int, height: Int): Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        // Lokasi restoran dari Intent
        val restaurantLocation = intent.getParcelableExtra<LatLng>("RESTAURANT_LOCATION")
        val restaurantName = intent.getStringExtra("RESTAURANT_NAME") ?: "Restoran"
        val restaurantAddress = intent.getStringExtra("RESTAURANT_ADDRESS") ?: "Alamat tidak tersedia"

        // Tambahkan marker lokasi restoran
        val smallIcon = BitmapDescriptorFactory.fromBitmap(getScaledBitmap(R.drawable.ic_lokasi_resto, 100, 100))
        restaurantLocation?.let {
            mMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .title(restaurantName)
                    .icon(smallIcon)
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))

            // Tampilkan alamat restoran
            binding.tvLokasiResto.text = "$restaurantName\n$restaurantAddress"
        }

        // Tambahkan lokasi saat ini
        val smallIconMaps = BitmapDescriptorFactory.fromBitmap(getScaledBitmap(R.drawable.ic_lokasi_saat, 100, 100))
        getCurrentLocation { currentLocation ->
            mMap.addMarker(
                MarkerOptions()
                    .position(currentLocation)
                    .title("Lokasi Saya")
                    .icon(smallIconMaps)
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))

            // Tampilkan nama lokasi saat ini
            val currentAddress = getAddressFromLatLng(currentLocation)
            binding.tvLokasiSaat.text = currentAddress

            // Gambar Polyline (Garis)
            restaurantLocation?.let { destination ->
                val polylineOptions = PolylineOptions()
                    .add(currentLocation) // Lokasi saat ini
                    .add(destination) // Lokasi restoran
                    .color(resources.getColor(R.color.primary))
                    .width(8f) // Ketebalan garis

                mMap.addPolyline(polylineOptions)

                val jarakKm = hitungJarakKm(currentLocation, destination)
                binding.tvJarak.text = String.format("Jarak: %.2f km", jarakKm)
            }
        }
    }

    private fun hitungJarakKm(start: LatLng, end: LatLng): Double {
        val radiusEarth = 6371 // radius bumi dalam KM
        val latDiff = Math.toRadians(end.latitude - start.latitude)
        val lngDiff = Math.toRadians(end.longitude - start.longitude)

        val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(start.latitude)) *
                Math.cos(Math.toRadians(end.latitude)) *
                Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radiusEarth * c // hasil dalam KM
    }

}