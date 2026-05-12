package com.oyn.rencangku.ui.preference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.oyn.rencangku.R

data class PreferenceStep(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val options: List<String>,
    val isRatingStep: Boolean = false
)

class PreferenceStepAdapter(
    private val context: Context,
    private val steps: List<PreferenceStep>,
    private val viewModel: PreferenceViewModel
) : RecyclerView.Adapter<PreferenceStepAdapter.StepViewHolder>() {

    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon       : ImageView  = itemView.findViewById(R.id.ivStepIcon)
        val tvTitle      : TextView   = itemView.findViewById(R.id.tvStepTitle)
        val tvSubtitle   : TextView   = itemView.findViewById(R.id.tvStepSubtitle)
        val chipGroup    : ChipGroup  = itemView.findViewById(R.id.chipGroup)
        val layoutRating : View       = itemView.findViewById(R.id.layoutRating)
        val ratingBar    : RatingBar  = itemView.findViewById(R.id.ratingBar)
        val tvRatingLabel: TextView   = itemView.findViewById(R.id.tvRatingLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_preference, parent, false)
        return StepViewHolder(view)
    }

    override fun getItemCount() = steps.size

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.tvTitle.text    = step.title
        holder.tvSubtitle.text = step.subtitle
        holder.ivIcon.setImageResource(step.iconRes)

        if (step.isRatingStep) {
            holder.chipGroup.visibility    = View.GONE
            holder.layoutRating.visibility = View.VISIBLE

            holder.ratingBar.rating = viewModel.selectedRating
            holder.tvRatingLabel.text = "Rating minimal: ${viewModel.selectedRating.toInt()}"

            holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                viewModel.selectedRating  = rating
                holder.tvRatingLabel.text = "Rating minimal: ${rating.toInt()}"
            }
        } else {
            holder.chipGroup.visibility    = View.VISIBLE
            holder.layoutRating.visibility = View.GONE
            holder.chipGroup.removeAllViews()

            step.options.forEach { option ->
                val chip = Chip(context).apply {
                    text            = option
                    isCheckable     = true
                    chipCornerRadius = 24f

                    // Gunakan color dari palette project (primary #9CAB84)
                    chipBackgroundColor = ContextCompat.getColorStateList(
                        context, R.color.chip_state_color
                    )
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.black)
                    )
                }

                // Restore state jika user kembali ke step sebelumnya
                chip.isChecked = when (position) {
                    0 -> viewModel.selectedCategory == option
                    1 -> viewModel.selectedPrice    == option
                    3 -> viewModel.selectedOpenTime == option
                    4 -> viewModel.selectedWeather  == option
                    else -> false
                }

                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        when (position) {
                            0 -> viewModel.selectedCategory  = option
                            1 -> viewModel.selectedPrice     = option
                            3 -> viewModel.selectedOpenTime  = option
                            4 -> viewModel.selectedWeather   = option
                        }
                    }
                }

                holder.chipGroup.addView(chip)
            }
        }
    }
}