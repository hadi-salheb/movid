package com.hadysalhab.movid.screen.common.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.BACKDROP_SIZE_780
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.rating.Rating

class MovieCardImpl(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    MovieCard() {
    private val movieImage: ImageView
    private val movieTitle: TextView
    private val movieReleaseDate: TextView
    private val ratingFL: FrameLayout
    private val rating: Rating
    private lateinit var movie: Movie

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_card, parent, false))
        movieImage = findViewById(R.id.iv_movie)
        movieTitle = findViewById(R.id.tv_movie_title)
        movieReleaseDate = findViewById(R.id.tv_released_movie)
        ratingFL = findViewById(R.id.rating_wrapper)
        rating = viewFactory.getRatingView(ratingFL)
        ratingFL.addView(rating.getRootView())
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onMovieCardClicked(movie.id)
            }
        }
    }


    override fun displayMovie(movie: Movie) {
        this.movie = movie
        movieTitle.text = movie.title
        movie.backdropPath?.let {
            Glide.with(getContext())
                .load(IMAGES_BASE_URL+ BACKDROP_SIZE_780+it)
                .into(movieImage)
        }
        movieReleaseDate.text = movie.releaseDate
        rating.displayRating(movie.voteAverage, movie.voteCount)
    }
}