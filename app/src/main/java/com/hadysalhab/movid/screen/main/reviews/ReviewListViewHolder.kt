package com.hadysalhab.movid.screen.main.reviews

import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.reviews.ReviewListItem

class ReviewListViewHolder(private val reviewListItem: ReviewListItem) :
    RecyclerView.ViewHolder(reviewListItem.getRootView()) {
    fun bind(review: Review) {
        reviewListItem.displayReview(review)
    }
}
