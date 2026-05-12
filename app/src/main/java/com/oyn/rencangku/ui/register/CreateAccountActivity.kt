package com.oyn.rencangku.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.oyn.rencangku.ui.login.SignInActivity
import com.oyn.rencangku.R
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.onboarding.ActivityOnboardingLast

class CreateAccountActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(ApiClient.RestaurantApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        val buttonBack = findViewById<ImageView>(R.id.Iv_back)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginText = findViewById<TextView>(R.id.loginTextView)
        val termsCheckbox = findViewById<CheckBox>(R.id.termsCheckbox)

        var isPasswordVisible = false

        // Toggle password
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            passwordInput.inputType =
                if (isPasswordVisible)
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            passwordToggle.setImageResource(
                if (isPasswordVisible) R.drawable.ic_visibility
                else R.drawable.ic_visibility_off
            )
            passwordInput.setSelection(passwordInput.text.length)
        }

        // REGISTER
        signUpButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString()
                .trim()
                .lowercase()
            val password = passwordInput.text.toString().trim()

            when {
                name.isEmpty() -> {
                    nameInput.error = "Nama tidak boleh kosong"
                    return@setOnClickListener
                }
                email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Email tidak valid"
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    passwordInput.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }
                !termsCheckbox.isChecked -> {
                    Toast.makeText(this, "Setujui syarat & ketentuan", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            signUpButton.isEnabled = false

            viewModel.signup(
                name, email, password,
                onSuccess = {
                    Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ActivityOnboardingLast::class.java))
                    finish()
                },
                onError = {
                    signUpButton.isEnabled = true
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            )
        }

        // KE LOGIN
        loginText.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        // BACK
        buttonBack.setOnClickListener {
            finish()
        }
    }
}
