package com.hadysalhab.movid.screen.common.movies

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie

class MovieCardImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : MovieCard() {
    private val movieImage: ImageView
    private val movieTitle: TextView
    private val movieOverview: TextView
    private val movieRating: RatingBar
    private val movieRatingText: TextView
    private val ratingWrapper: LinearLayout
    private val movieReleaseDate: TextView
    private lateinit var movie: Movie

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_card, parent, false))
        movieImage = findViewById(R.id.iv_movie)
        movieTitle = findViewById(R.id.tv_movie_title)
        movieOverview = findViewById(R.id.tv_overview)
        movieRating = findViewById(R.id.rating_movie)
        movieRatingText = findViewById(R.id.tv_rating_movie)
        movieReleaseDate = findViewById(R.id.tv_released_movie)
        ratingWrapper = findViewById(R.id.rating_wrapper)
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onMovieCardClicked(movie.id.toInt())
            }
        }
    }


    override fun displayMovie(movie: Movie) {
        this.movie = movie
        movieTitle.text = movie.title
        movieOverview.text = movie.overview
        movie.posterPath?.let {
            Log.d("poster", "displayMovie: ${it} ")
            Glide.with(getContext())
                .load(it)
                .into(movieImage)
        }
        movieReleaseDate.text = movie.releaseDate

        if (movie.voteCount == 0) {
            movieRating.visibility = View.GONE
            movieRatingText.text = "(Rating Not Available)"
        } else {
            val number3digits: Double = String.format("%.3f", movie.voteAverage / 2).toDouble()
            val number2digits: Double = String.format("%.2f", number3digits).toDouble()
            val result: Double = String.format("%.1f", number2digits).toDouble()
            movieRating.rating = result.toFloat()
            movieRatingText.text = "$result (${movie.voteCount})"
        }

    }
}