package com.hadysalhab.movid.screen.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.mancj.materialsearchbar.MaterialSearchBar


class SearchViewImpl(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    SearchView(), GenreAdapter.Listener {
    private val materialSearchBar: MaterialSearchBar
    private val recyclerView: RecyclerView
    private val genreAdapter: GenreAdapter

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_search, parent, false))
        materialSearchBar = findViewById(R.id.searchBar)
        recyclerView = findViewById(R.id.recycler_view)
        genreAdapter = GenreAdapter(this, viewFactory)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = genreAdapter
        }
        genreAdapter.submitList(Genre.values().toList())
    }

}