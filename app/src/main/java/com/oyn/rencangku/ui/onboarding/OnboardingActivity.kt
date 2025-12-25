package com.oyn.rencangku.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oyn.rencangku.R
import com.oyn.rencangku.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    private val layouts = listOf(
        R.layout.onboarding_1,
        R.layout.onboarding_2,
        R.layout.onboarding_3
    )

    companion object {
        private const val PREF_NAME = "onboarding_pref"
        private const val KEY_ONBOARDING_SHOWN = "onboarding_shown"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isOnboardingShown()) {
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
        setOnboardingShown()
        navigateToNext()
    }

    private fun navigateToNext() {
        startActivity(Intent(this, ActivityOnboardingLast::class.java))
        finish()
    }

    private fun isOnboardingShown(): Boolean {
        val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(KEY_ONBOARDING_SHOWN, false)
    }

    private fun setOnboardingShown() {
        val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean(KEY_ONBOARDING_SHOWN, true).apply()
    }
}
