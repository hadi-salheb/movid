package com.hadysalhab.movid.screen.main.featured

import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.MovieGroup

class MovieGroupViewHolder(private val movieGroupView: MovieGroupItemView) :
    RecyclerView.ViewHolder(movieGroupView.getRootView()) {
    fun bind(movieGroup: MovieGroup) {
        movieGroupView.displayMovieGroup(movieGroup)
    }
}