package com.hadysalhab.movid.screen.main.movielist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.common.constants.POSTER_SIZE_92
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory

class MovieListItemViewImpl(
    layoutInflater: LayoutInflater,
    private val parent: ViewGroup?,
    private val viewFactory: ViewFactory
) :
    MovieListItemView() {
    private val movieImageIV: ImageView
    private val movieTitleTV: TextView
    private val releasedTV: TextView
    private val ratingFL: FrameLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_list_item, parent, false))
        movieImageIV = findViewById(R.id.iv_movie)
        movieTitleTV = findViewById(R.id.tv_movie_title)
        releasedTV = findViewById(R.id.tv_released_movie)
        ratingFL = findViewById(R.id.rating_wrapper)
    }

    override fun displayMovie(movie: Movie) {
        movie.posterPath?.let {
            Glide.with(getContext())
                .load(IMAGES_BASE_URL + POSTER_SIZE_92 + it)
                .into(movieImageIV)
        }
        movieTitleTV.text = movie.title
        releasedTV.text = movie.releaseDate
        val rating = viewFactory.getRatingView(parent)
        rating.displayRating(movie.voteAverage, movie.voteCount)
        ratingFL.removeAllViews()
        ratingFL.addView(rating.getRootView())
    }
}