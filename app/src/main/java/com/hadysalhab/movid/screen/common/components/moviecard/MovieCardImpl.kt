package com.hadysalhab.movid.screen.common.components.moviecard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie

class MovieCardImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : MovieCard() {
    private val movieImage: ImageView
    private val movieTitle: TextView
    private lateinit var movie: Movie

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_card, parent, false))
        movieImage = findViewById(R.id.iv_movie)
        movieTitle = findViewById(R.id.tv_movie_title)
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onMovieCardClicked (movie.id.toInt())
            }
        }
    }


    override fun displayMovie(movie: Movie) {
        this.movie = movie
        movieTitle.text = movie.title
        //TODO:DISPLAY MOVIE IMAGE
    }
}