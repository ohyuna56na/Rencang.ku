package com.oyn.rencangku.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.ActivityEditProfileBinding
import com.oyn.rencangku.network.ApiClient
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sessionManager: SessionManager
    private var selectedImageUri: Uri? = null

    // Launcher untuk mengambil gambar dari galeri HP
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.imgAvatarProfile.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Terapkan penyesuaian Safe Area (Status Bar & Nav Bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(0, statusBar.top, 0, navBar.bottom)
            insets
        }

        // Muat data profil user saat ini ke form input
        loadCurrentUserData()

        // Listener Aksi Komponen Tampilan
        binding.btnBack.setOnClickListener { finish() }
        binding.btnChangeAvatar.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnSaveProfile.setOnClickListener { saveProfileChanges() }
    }

    private fun loadCurrentUserData() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        binding.progressLoading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = ApiClient.RestaurantApi.getProfile(
                    apiKey = ApiClient.API_KEY,
                    auth = "Bearer ${ApiClient.API_KEY}",
                    id = "eq.$userId"
                )
                if (response.isNotEmpty()) {
                    val user = response.first()
                    binding.etEditName.setText(user.name)
                    binding.etEditEmail.setText(user.email)

                    if (response.isNotEmpty()) {
                        val user = response.first()
                        binding.etEditName.setText(user.name)
                        binding.etEditEmail.setText(user.email)

                        if (!user.avatar.isNullOrEmpty()) {
                            Glide.with(this@EditProfileActivity)
                                .load(user.avatar)
                                .placeholder(R.drawable.profile) // Gambar sementara saat loading
                                .error(R.drawable.profile)       // Gambar jika URL error
                                .into(binding.imgAvatarProfile)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Gagal mengambil profil data lama: ${e.message}")
            } finally {
                binding.progressLoading.visibility = View.GONE
            }
        }
    }

    private fun saveProfileChanges() {
        val userId = sessionManager.getUserId()
        val newName = binding.etEditName.text.toString().trim()
        val newEmail = binding.etEditEmail.text.toString().trim()
        val newPassword = binding.etEditPassword.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Nama dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSaveProfile.visibility = View.GONE
        binding.progressLoading.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Menyiapkan data tubuh (body request) secara fleksibel
                val updateData = mutableMapOf<String, String>()
                updateData["name"] = newName
                updateData["email"] = newEmail

                if (newPassword.isNotEmpty()) {
                    updateData["password"] = newPassword
                }

                if (selectedImageUri != null) {
                    // Tip: Jika ingin mengupload file gambar asli ke Supabase Storage,
                    // simpan file-nya ke bucket dan masukkan URL string hasil upload ke field di bawah ini.
                    updateData["avatar"] = selectedImageUri.toString()
                }

                val response = ApiClient.RestaurantApi.updateProfile(
                    id = "eq.$userId",
                    request = updateData
                )

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    Toast.makeText(this@EditProfileActivity, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProfileActivity, "Gagal memperbarui database profil", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Error sewaktu menyimpan perubahan: ${e.message}")
                Toast.makeText(this@EditProfileActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSaveProfile.visibility = View.VISIBLE
                binding.progressLoading.visibility = View.GONE
            }
        }
    }
}