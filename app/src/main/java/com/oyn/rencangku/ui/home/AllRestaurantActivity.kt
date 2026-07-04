package com.oyn.rencangku.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.ActivityAllRestaurantBinding
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.detailResto.DetailRestoActivity

class AllRestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllRestaurantBinding

    private lateinit var adapter: CulinaryAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var sessionManager: SessionManager
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityAllRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        val factory = HomeViewModelFactory(
            ApiClient.RestaurantApi,
            sessionManager
        )

        viewModel = ViewModelProvider(
            this,
            factory
        )[HomeViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        category = intent.getStringExtra("CATEGORY")

        binding.detailTitle.text = category ?: "Semua Restoran"

        if (category.isNullOrEmpty()) {
            viewModel.loadCulinaryPlaces()
        } else {
            viewModel.loadCategory(category!!)
        }
    }

    private fun setupToolbar() {
        binding.backButtonDetail.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {

        adapter = CulinaryAdapter { culinary ->

            val intent = Intent(
                this,
                DetailRestoActivity::class.java
            )

            intent.putExtra(
                "SELECTED_RESTAURANT",
                culinary
            )

            startActivity(intent)
        }

        binding.rvRestaurant.layoutManager =
            GridLayoutManager(this, 2)

        binding.rvRestaurant.adapter = adapter
    }

    private fun observeViewModel() {

        viewModel.culinaryPlaces.observe(this) { list ->

            adapter.submitList(list)

            binding.tvNoData.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) {

            binding.progressBar.visibility =
                if (it) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) {

            binding.tvNoData.visibility = View.VISIBLE
            binding.tvNoData.text = it
        }
    }
}