package com.oyn.rencangku.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.databinding.ActivitySearchBinding
import com.oyn.rencangku.ui.detailResto.DetailRestoActivity
import com.oyn.rencangku.ui.home.CulinaryAdapter
import com.oyn.rencangku.ui.home.HomeRepository
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var adapter: CulinaryAdapter

    private lateinit var repository: HomeRepository

    private var restaurantList = listOf<CulinaryPlace>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySafeArea()

        repository = HomeRepository(SessionManager(this))

        setupRecycler()

        loadRestaurant()

        binding.etSearch.addTextChangedListener {

            filterRestaurant(it.toString())

        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycler() {

        adapter = CulinaryAdapter { restaurant ->

            val intent = Intent(this, DetailRestoActivity::class.java)

            intent.putExtra("SELECTED_RESTAURANT", restaurant)

            startActivity(intent)

        }

        binding.rvSearchResults.layoutManager =
            GridLayoutManager(this,2)

        binding.rvSearchResults.adapter = adapter

    }

    private fun loadRestaurant() {

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {

            try {

                restaurantList = repository.getCulinaryPlaces()

                adapter.submitList(restaurantList)

            } finally {

                binding.progressBar.visibility = View.GONE

            }

        }

    }

    private fun filterRestaurant(keyword:String){

        val query = keyword.trim().lowercase()

        val result = if(query.isBlank()){

            restaurantList

        }else{

            restaurantList.filter {

                it.displayTitle.lowercase().contains(query)

                        ||

                        it.displayAddress.lowercase().contains(query)

                        ||

                        it.displayCategory.lowercase().contains(query)

            }

        }

        binding.tvNoResults.visibility =
            if(result.isEmpty()) View.VISIBLE else View.GONE

        adapter.submitList(result)

    }

    private fun applySafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                view.paddingBottom
            )

            insets
        }
    }
}