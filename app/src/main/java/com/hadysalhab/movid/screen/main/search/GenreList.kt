package com.hadysalhab.movid.screen.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class GenreList(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    BaseObservableViewMvc<GenreList.Listener>(),
    GenreAdapter.Listener {
    interface Listener {
        fun onGenreListItemClick(genre: Genre)
    }

    private val recyclerView: RecyclerView
    private val genreAdapter: GenreAdapter

    init {
        setRootView(layoutInflater.inflate(R.layout.recyclerview, parent, false))
        recyclerView = findViewById(R.id.rv)
        genreAdapter = GenreAdapter(this, viewFactory)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@GenreList.genreAdapter
        }
        genreAdapter.submitList(Genre.values().toList())
    }

    override fun onGenreListItemClick(genre: Genre) {
        listeners.forEach {
            it.onGenreListItemClick(genre)
        }
    }

}