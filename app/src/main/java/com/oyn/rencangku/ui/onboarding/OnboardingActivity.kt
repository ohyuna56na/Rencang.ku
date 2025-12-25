package com.oyn.rencangku.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oyn.rencangku.MainActivity
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    private val layouts = listOf(
        R.layout.onboarding_1,
        R.layout.onboarding_2,
        R.layout.onboarding_3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        if (sessionManager.isLogin()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        if (sessionManager.isOnboardingShown()) {
            navigateToNext()
            return
        }

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingAdapter = OnboardingAdapter(layouts)
        binding.viewPager.adapter = onboardingAdapter

        binding.nextButton.setOnClickListener {
            val nextPage = binding.viewPager.currentItem + 1
            if (nextPage < layouts.size) {
                binding.viewPager.currentItem = nextPage
            } else {
                finishOnboarding()
            }
        }

        binding.skipButton.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        val sessionManager = SessionManager(this)
        sessionManager.setOnboardingShown(true)

        navigateToNext()
    }

    private fun navigateToNext() {
        startActivity(Intent(this, ActivityOnboardingLast::class.java))
        finish()
    }
}
