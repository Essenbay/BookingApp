package com.example.bookingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingapp.data.models.ReviewUI
import com.example.bookingapp.databinding.ReviewItemBinding
import com.example.bookingapp.util.formatDate

class ReviewsAdapter(
    private val list: List<ReviewUI>
) : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReviewItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class ViewHolder(private val binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewUI) {
            binding.username.text = review.username
            val creationDate =
                formatDate(review.review.dateOfCreation.toDate())
            binding.creationDate.text = creationDate
            binding.rate.rating = review.review.rate
            binding.comment.text = review.review.comment
        }
    }
}