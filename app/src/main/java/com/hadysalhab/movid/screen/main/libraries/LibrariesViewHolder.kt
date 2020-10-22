package com.hadysalhab.movid.screen.main.libraries

import androidx.recyclerview.widget.RecyclerView


class LibraryViewHolder(private val libraryListItem: LibraryListItem) :
    RecyclerView.ViewHolder(libraryListItem.getRootView()) {
    fun bind(library: Library) {
        libraryListItem.displayLibrary(library)
    }
}

