package com.hadysalhab.movid.screen.main.reviews

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.ViewFactory

class ReviewListAdapter(private val viewFactory: ViewFactory) :
    ListAdapter<Review, ReviewListViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        val view = viewFactory.getReviewListItem(parent)
        return ReviewListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}