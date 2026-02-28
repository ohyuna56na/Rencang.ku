package com.oyn.rencangku.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.databinding.FragmentFavoriteBinding
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.ui.detailResto.DetailRestoActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val apiService = ApiClient.RestaurantApi
        val sessionManager = SessionManager(requireContext())
        val repository = FavoriteRepository(apiService, sessionManager)
        val factory = FavoriteViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        applySafeArea()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadFavorites()

        return binding.root
    }

    private fun applySafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.favoriteTitle) { v, insets ->
            val topInset =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, topInset, 0, 0)
            insets
        }
    }

    private fun setupRecyclerView() {

        adapter = FavoriteAdapter(

            onItemClick = { place ->

                val intent = Intent(requireContext(), DetailRestoActivity::class.java)
                intent.putExtra("SELECTED_RESTAURANT", place)
                startActivity(intent)
            },

            onDeleteClick = { place ->
                viewModel.deleteFavorite(place.id)
            }
        )

        binding.recyclerViewFavorite.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.recyclerViewFavorite.adapter = adapter
    }
    private fun observeViewModel() {
        viewModel.favorites.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.tvEmptyFavorites.visibility = View.VISIBLE
                binding.recyclerViewFavorite.visibility = View.GONE
            } else {
                binding.tvEmptyFavorites.visibility = View.GONE
                binding.recyclerViewFavorite.visibility = View.VISIBLE
                adapter.submitList(list)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
