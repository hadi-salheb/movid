package com.hadysalhab.movid.screen.common.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Review

class ReviewListItemImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : ReviewListItem() {
    private val authorTV: TextView
    private val reviewTV: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_review_list_item, parent, false))
        authorTV = findViewById(R.id.author_name)
        reviewTV = findViewById(R.id.author_review)
    }

    override fun displayReview(review: Review) {
        authorTV.text = review.author
        reviewTV.text = review.content
    }
}