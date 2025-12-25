package com.oyn.rencangku.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.oyn.rencangku.R
import com.oyn.rencangku.ui.login.SignInActivity
import com.oyn.rencangku.ui.register.CreateAccountActivity

class ActivityOnboardingLast : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_last)

        findViewById<Button>(R.id.signInButton).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
        applySafeArea()
    }

    private fun applySafeArea() {
        val logoImageView = findViewById<View>(R.id.logoImageView)

        ViewCompat.setOnApplyWindowInsetsListener(logoImageView) { v, insets ->
            val topInset =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, topInset, 0, 0)
            insets
        }
    }
}