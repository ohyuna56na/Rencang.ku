package com.oyn.rencangku.ui.home

import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.R
import com.oyn.rencangku.databinding.ItemRestoranBinding

class CulinaryViewHolder(
    private val binding: ItemRestoranBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CulinaryPlace) {

        binding.tvRestaurantName.text = item.title
        binding.tvRestaurantAddress.text =
            item.address ?: "-"

        binding.tvRatings.text =
            item.rating ?: "0.0"

        binding.tvCategoriSuhu.text =
            item.categorize_weather ?: ""

        // Image
        Glide.with(binding.root.context)
            .load(item.header_image)
            .placeholder(R.drawable.img)
            .into(binding.imgProfile)
    }
}
