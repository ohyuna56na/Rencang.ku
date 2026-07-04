package com.oyn.rencangku.ui.home.category

import androidx.recyclerview.widget.RecyclerView
import com.oyn.rencangku.databinding.ItemCategoryBinding

class CategoryViewHolder(
    private val binding: ItemCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(category: String) {

        binding.tvCategory.text = category

    }
}