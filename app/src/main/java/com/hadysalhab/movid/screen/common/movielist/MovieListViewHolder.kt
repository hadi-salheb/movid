package com.hadysalhab.movid.screen.common.movielist

import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.movies.MovieListItem

class MovieListViewHolder(private val movieListItem: MovieListItem) :
    RecyclerView.ViewHolder(movieListItem.getRootView()) {
    fun bind(movie: Movie) {
        movieListItem.displayMovie(movie)
    }
}
