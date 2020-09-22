package com.hadysalhab.movid.screen.main.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.screen.common.listheader.ListHeader

sealed class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view)

class GenreItemViewHolder(private val genreListItem: GenreListItem) :
    GenreViewHolder(genreListItem.getRootView()) {
    fun bind(genre: Genre) {
        genreListItem.displayGenre(genre)
    }

}

class GenreHeaderViewHolder(listHeader: ListHeader) :
    GenreViewHolder(listHeader.getRootView())