package com.hadysalhab.movid.screen.main.reviews

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.loading.LoadingView
import com.hadysalhab.movid.screen.common.paginationerror.PaginationError
import com.hadysalhab.movid.screen.common.reviews.ReviewListItem

sealed class ReviewListViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(review: Review) {

    }

    class ReviewViewHolder(private val reviewListItem: ReviewListItem) :
        ReviewListViewHolder(reviewListItem.getRootView()) {
        override fun bind(review: Review) {
            super.bind(review)
            reviewListItem.displayReview(review)
        }
    }

    class LoadingViewHolder(loadingView: LoadingView) :
        ReviewListViewHolder(loadingView.getRootView())

    class PaginationErrorViewHolder(paginationError: PaginationError) :
        ReviewListViewHolder(paginationError.getRootView())
}
