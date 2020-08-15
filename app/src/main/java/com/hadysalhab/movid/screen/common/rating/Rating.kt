package com.hadysalhab.movid.screen.common.rating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

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
            movieRatingText.text = "(Rating Not Available)"
        } else {
            val number3digits: Double = String.format("%.3f", voteAvg / 2).toDouble()
            val number2digits: Double = String.format("%.2f", number3digits).toDouble()
            val result: Double = String.format("%.1f", number2digits).toDouble()
            movieRating.rating = result.toFloat()
            movieRatingText.text = "$result (${voteAvg})"
        }
    }
}