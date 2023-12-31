package com.hadysalhab.movid.screen.common.rating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc
import kotlin.math.round

class Rating(layoutInflater: LayoutInflater, parent: ViewGroup?) : BaseViewMvc() {
    private val movieRating: RatingBar
    private val movieRatingText: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_rating, parent, false))
        movieRating = findViewById(R.id.rating_movie)
        movieRatingText = findViewById(R.id.tv_rating_movie)
    }

    fun displayRating(voteAvg: Double, voteCount: Int) {
        if (voteCount == 0) {
            movieRating.visibility = View.GONE
            movieRatingText.text = getContext().getString(R.string.no_rating)
        } else {
            movieRating.rating = round((voteAvg / (2 * .5)).toFloat()) * .5f
            movieRatingText.text = getContext().getString(R.string.vote_summary, voteAvg, voteCount)
        }
    }
}