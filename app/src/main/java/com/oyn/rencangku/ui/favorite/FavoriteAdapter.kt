package com.oyn.rencangku.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.data.Favorite
import com.oyn.rencangku.databinding.ItemFavoriteBinding

class FavoriteAdapter(
    private val onItemClick: (CulinaryPlace) -> Unit,
    private val onDeleteClick: (Favorite) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private val list = mutableListOf<Favorite>()

    fun submitList(data: List<Favorite>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Favorite) {

            val place = item._culinary_places ?: return

            binding.tvRestaurantName.text = place.title
            binding.tvRestaurantAddress.text = place.address
            binding.TvRatings.text = place.rating?.let { String.format("%.1f ⭐", it) } ?: "-"
            binding.tvCategoriSuhu.text = place.categorize_weather

            binding.root.setOnClickListener {
                onItemClick(place)
            }

            binding.imgFavorite.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
