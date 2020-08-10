package com.hadysalhab.movid.screen.main.featured

import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.MovieGroup

class MovieGroupViewHolder(private val moviesView: MoviesView) :
    RecyclerView.ViewHolder(moviesView.getRootView()) {
    fun bind(movieGroup: MovieGroup) {
        moviesView.displayCardGroup(movieGroup)
    }
}