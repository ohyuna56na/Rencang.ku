package com.oyn.rencangku.ui.preference

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.oyn.rencangku.MainActivity
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager

class PreferenceActivity : AppCompatActivity() {

    private val viewModel: PreferenceViewModel by viewModels()
    private lateinit var session: SessionManager

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button
    private lateinit var progressBar: ProgressBar

    private val stepDots by lazy {
        listOf(
            findViewById<View>(R.id.step1Dot),
            findViewById<View>(R.id.step2Dot),
            findViewById<View>(R.id.step3Dot),
            findViewById<View>(R.id.step4Dot),
            findViewById<View>(R.id.step5Dot)
        )
    }

    private val steps = listOf(
        PreferenceStep(
            title    = "Kategori Favorit",
            subtitle = "Pilih jenis kuliner yang paling kamu sukai",
            iconRes  = R.drawable.ic_category,
            options  = listOf(
                "Bakso & Mie", "Fast Food", "Seafood",
                "Steak", "Korean", "Pizza",
                "Chinese", "Cafe", "Healthy", "Bakery & Dessert",
                "Indonesia", "Japanese", "Chicken", "Satay",
                "Martabak", "Steak", "Food Court", "Other"
            )
        ),
        PreferenceStep(
            title    = "Kisaran Harga",
            subtitle = "Berapa budget yang biasa kamu siapkan?",
            iconRes  = R.drawable.ic_money,
            options  = listOf(
                "Murah (< 25rb)",
                "Sedang (25–50rb)",
                "Mahal (50–100rb)",
                "Premium (> 100rb)"
            )
        ),
        PreferenceStep(
            title        = "Rating Minimal",
            subtitle     = "Berapa bintang minimal tempat makan pilihanmu?",
            iconRes      = R.drawable.ic_star,
            options      = emptyList(),
            isRatingStep = true
        ),
        PreferenceStep(
            title    = "Waktu Buka",
            subtitle = "Kapan kamu biasanya mau makan?",
            iconRes  = R.drawable.ic_clock,
            options  = listOf("Pagi", "Siang", "Malam", "24 Jam")
        ),
        PreferenceStep(
            title    = "Cuaca Favorit",
            subtitle = "Di cuaca seperti apa kamu sering makan di luar?",
            iconRes  = R.drawable.ic_cloudy,
            options  = listOf("Dingin", "Panas", "Semua Cuaca")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        session     = SessionManager(this)
        viewPager   = findViewById(R.id.viewPager)
        btnNext     = findViewById(R.id.btnNext)
        btnBack     = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)

        setupViewPager()
        setupButtons()
        observeViewModel()
    }

    private fun setupViewPager() {
        viewPager.adapter          = PreferenceStepAdapter(this, steps, viewModel)
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateStepIndicator(position)
                updateButtons(position)
            }
        })
        updateStepIndicator(0)
        updateButtons(0)
    }

    private fun setupButtons() {
        btnNext.setOnClickListener {
            val current = viewPager.currentItem
            if (current < steps.lastIndex) {
                if (isCurrentStepValid(current)) {
                    viewPager.currentItem = current + 1
                } else {
                    Toast.makeText(this, "Pilih salah satu opsi dulu ya!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (isCurrentStepValid(current)) savePreference()
                else Toast.makeText(this, "Pilih salah satu opsi dulu ya!", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            val current = viewPager.currentItem
            if (current > 0) viewPager.currentItem = current - 1
        }
    }

    private fun isCurrentStepValid(position: Int): Boolean = when (position) {
        0    -> viewModel.selectedCategory.isNotBlank()
        1    -> viewModel.selectedPrice.isNotBlank()
        2    -> viewModel.selectedRating > 0f
        3    -> viewModel.selectedOpenTime.isNotBlank()
        4    -> viewModel.selectedWeather.isNotBlank()
        else -> true
    }

    private fun savePreference() {
        val userId = session.getUserId()
        if (userId == -1) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.savePreference(userId)
    }

    private fun observeViewModel() {
        viewModel.savePreferenceState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnNext.isEnabled      = false
                }
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    btnNext.isEnabled      = true
                    Toast.makeText(this, "Preferensi tersimpan! 🎉", Toast.LENGTH_SHORT).show()
                    goToMain()
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    btnNext.isEnabled      = true
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateStepIndicator(position: Int) {
        stepDots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(
                when {
                    index == position -> R.drawable.dot_active
                    index < position  -> R.drawable.dot_done
                    else              -> R.drawable.dot_inactive
                }
            )
        }
    }

    private fun updateButtons(position: Int) {
        btnBack.visibility = if (position > 0) View.VISIBLE else View.GONE
        btnNext.text       = if (position == steps.lastIndex) "Mulai Jelajahi 🎉" else "Selanjutnya"
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}