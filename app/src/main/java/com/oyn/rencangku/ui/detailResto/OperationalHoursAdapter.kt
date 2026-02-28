package com.oyn.rencangku.ui.detailResto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oyn.rencangku.databinding.ItemOperationalHoursBinding

class OperationalHoursAdapter(
    private val list: List<Pair<String, String>>
) : RecyclerView.Adapter<OperationalHoursAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOperationalHoursBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOperationalHoursBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvDay.text = item.first
        holder.binding.tvTime.text = item.second
    }
}