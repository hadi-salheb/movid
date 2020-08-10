package com.hadysalhab.movid.screen.main.featured

import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.MovieGroup

class MovieGroupViewHolder(private val cardGroupView: CardGroupView) :
    RecyclerView.ViewHolder(cardGroupView.getRootView()) {
    fun bind(movieGroup: MovieGroup) {
        cardGroupView.displayMovieGroup(movieGroup)
    }
}