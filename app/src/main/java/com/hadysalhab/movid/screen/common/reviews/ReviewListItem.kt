package com.hadysalhab.movid.screen.common.reviews

import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

abstract class ReviewListItem : BaseViewMvc() {
    abstract fun displayReview(review: Review)
}