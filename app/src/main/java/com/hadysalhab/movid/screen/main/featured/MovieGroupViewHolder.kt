package com.hadysalhab.movid.screen.main.featured

import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.cardgroup.DataGroup
import com.hadysalhab.movid.screen.common.cardgroup.MoviesView

class MovieGroupViewHolder(private val moviesView: MoviesView) :
    RecyclerView.ViewHolder(moviesView.getRootView()) {
    fun bind(movieGroup: MoviesResponse) {
        moviesView.renderData(DataGroup(movieGroup.tag, movieGroup.movies ?: emptyList()), 5)
    }
}