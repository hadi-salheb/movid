package com.hadysalhab.movid.screen.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hadysalhab.movid.R

class GenreListItemImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : GenreListItem() {
    private val genreIcon: ImageView
    private val genreText: TextView
    private val genreArrow: ImageView
    private lateinit var genre: Genre

    init {
        setRootView(layoutInflater.inflate(R.layout.component_genre_list_item, parent, false))
        genreIcon = findViewById(R.id.genre_icon)
        genreText = findViewById(R.id.genre_text)
        genreArrow = findViewById(R.id.genre_arrow)
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onGenreListItemClick(this.genre)
            }
        }
    }

    override fun displayGenre(genre: Genre) {
        this.genre = genre
        genreIcon.setImageResource(genre.icon)
        genreText.text = genre.genreName
    }


}