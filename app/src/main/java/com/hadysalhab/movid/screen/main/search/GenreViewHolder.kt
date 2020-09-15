package com.hadysalhab.movid.screen.main.search

import androidx.recyclerview.widget.RecyclerView

class GenreViewHolder(private val genreListItem: GenreListItem) :
    RecyclerView.ViewHolder(genreListItem.getRootView()) {
    fun bind(genre: Genre) {
        genreListItem.displayGenre(genre)
    }
}
