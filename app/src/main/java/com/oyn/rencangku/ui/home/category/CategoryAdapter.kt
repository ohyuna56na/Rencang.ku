package com.oyn.rencangku.ui.home.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oyn.rencangku.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val items = mutableListOf<String>()

    fun submitList(data: List<String>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {

        val category = items[position]

        holder.bind(category)

        holder.itemView.setOnClickListener {
            onClick(category)
        }
    }

    override fun getItemCount() = items.size
}