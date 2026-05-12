package com.oyn.rencangku.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.oyn.rencangku.MainActivity
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.ActivitySignInBinding
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.register.CreateAccountActivity
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var sessionManager: SessionManager

    private val viewModel: SignInViewModel by viewModels {
        SignInViewModelFactory(ApiClient.RestaurantApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        // Sudah login → langsung ke MainActivity
        // Cek preference dilakukan 1x di HomeFragment saja
        if (sessionManager.isLogin()) {
            goToMain()
            return
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPasswordToggle()
        setupAction()
        observeViewModel()
    }

    private fun setupPasswordToggle() {
        var isVisible = false
        binding.passwordToggleLogin.setOnClickListener {
            isVisible = !isVisible
            binding.passwordInputLogin.inputType =
                if (isVisible)
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            binding.passwordToggleLogin.setImageResource(
                if (isVisible) R.drawable.ic_visibility
                else R.drawable.ic_visibility_off
            )
            binding.passwordInputLogin.setSelection(
                binding.passwordInputLogin.text.length
            )
        }
    }

    private fun setupAction() {
        binding.LoginButton.setOnClickListener {
            val email    = binding.emaillogin.text.toString().trim()
            val password = binding.passwordInputLogin.text.toString().trim()

            when {
                email.isEmpty() ->
                    binding.emaillogin.error = "Email wajib diisi"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    binding.emaillogin.error = "Email tidak valid"
                password.isEmpty() ->
                    binding.passwordInputLogin.error = "Password wajib diisi"
                else ->
                    viewModel.login(email, password)
            }
        }

        binding.signupTextView.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }

        binding.IvBack.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) {
            binding.LoginButton.isEnabled = !it
        }

        viewModel.loginResult.observe(this) { result ->
            if (result != null) {
                lifecycleScope.launch {
                    try {
                        sessionManager.saveUserLogin(result.id)
                        sessionManager.setLogin(true)
                        Log.d("SESSION", "UserID: ${result.id}")

                        Toast.makeText(
                            this@SignInActivity,
                            "Login berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Langsung ke MainActivity — HomeFragment yang akan cek preference
                        goToMain()

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@SignInActivity,
                            "Gagal ambil profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}