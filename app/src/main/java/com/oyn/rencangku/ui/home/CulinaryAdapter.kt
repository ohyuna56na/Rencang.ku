package com.oyn.rencangku.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.databinding.ItemRestoranBinding

class CulinaryAdapter(
    private val onClick: (CulinaryPlace) -> Unit
) : RecyclerView.Adapter<CulinaryViewHolder>() {

    private val items = mutableListOf<CulinaryPlace>()

    fun submitList(data: List<CulinaryPlace>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CulinaryViewHolder {
        val binding = ItemRestoranBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CulinaryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CulinaryViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = items.size
}
