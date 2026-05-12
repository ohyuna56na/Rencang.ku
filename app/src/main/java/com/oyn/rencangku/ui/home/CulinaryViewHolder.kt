package com.oyn.rencangku.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.oyn.rencangku.R
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.databinding.ItemRestoranBinding

class CulinaryViewHolder(
    private val binding: ItemRestoranBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CulinaryPlace) {
        // Pakai display properties — otomatis ambil dari ML API atau Supabase
        binding.tvRestaurantName.text    = item.displayTitle
        binding.tvRestaurantAddress.text = item.displayAddress.ifEmpty { "-" }
        binding.tvRatings.text           = "${item.displayRating} ⭐"
        binding.tvCategoriSuhu.text      = item.displayWeather

        // Gambar hanya ada dari Supabase (headerImage)
        val imageUrl = item.headerImage
        if (!imageUrl.isNullOrEmpty()) {
            val glideUrl = GlideUrl(
                imageUrl,
                LazyHeaders.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build()
            )
            Glide.with(binding.root.context)
                .load(glideUrl)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(binding.imgProfile)
        } else {
            Glide.with(binding.root.context)
                .load(R.drawable.img)
                .into(binding.imgProfile)
        }
    }
}