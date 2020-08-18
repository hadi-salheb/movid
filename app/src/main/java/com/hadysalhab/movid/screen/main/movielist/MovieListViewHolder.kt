package com.hadysalhab.movid.screen.main.movielist

import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.Movie

class MovieListViewHolder(private val movieListItemView: MovieListItemView) :
    RecyclerView.ViewHolder(movieListItemView.getRootView()) {
    fun bind(movie: Movie) {
        movieListItemView.displayMovie(movie)
    }
}
