package com.hadysalhab.movid.screen.main.reviews

import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.movies.ReviewResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class ReviewListView : BaseObservableViewMvc<ReviewListView.Listener>() {
    interface Listener {
        fun loadMoreItems()
    }

    abstract fun displayReviews(reviews: List<Review>)
    abstract fun displayPaginationLoading()
    abstract fun displayLoadingIndicator()
}