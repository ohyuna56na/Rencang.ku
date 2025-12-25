package com.oyn.rencangku.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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
    }
}