package com.oyn.rencangku.ui.home

import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
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

        val glideUrl = GlideUrl(
            item.header_image,
            LazyHeaders.Builder()
                .addHeader("User-Agent", "Mozilla/5.0")
                .build()
        )

        // Image
        Glide.with(binding.root.context)
            .load(glideUrl)
            .placeholder(R.drawable.img)
            .error(R.drawable.img)
            .into(binding.imgProfile)
    }
}
