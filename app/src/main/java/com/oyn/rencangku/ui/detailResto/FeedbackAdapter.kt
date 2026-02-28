package com.oyn.rencangku.ui.detailResto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oyn.rencangku.R
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.Review
import java.text.SimpleDateFormat
import java.util.*

class FeedbackAdapter(
    private val reviews: List<Review>,
    private val sessionManager: SessionManager,
    private val onEditClick: (Review) -> Unit,
    private val onDeleteClick: (Review) -> Unit
) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    inner class FeedbackViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val feedbackTime: TextView = itemView.findViewById(R.id.feedbackTime)
        val feedbackDescription: TextView = itemView.findViewById(R.id.feedbackDescription)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {

        val review = reviews[position]
        val currentUserId = sessionManager.getUserId()

        holder.userImage.setImageResource(R.drawable.profile)
        holder.userName.text = "User ${review.users_id}"
        holder.feedbackDescription.text = review.review_text ?: "-"
        holder.ratingBar.rating = review.rating.toFloat()

        holder.feedbackTime.text = formatDate(review.created_at)

        // 🔥 Tampilkan edit & delete hanya jika pemilik review
        if (review.users_id == currentUserId) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
        } else {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(review)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(review)
        }
    }

    override fun getItemCount(): Int = reviews.size

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)

            val outputFormat =
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
}