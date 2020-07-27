package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.adapters.MovieAdapter

class MovieGroupItemViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) :
    MovieGroupItemView(), MovieAdapter.Listener {
    private val recyclerView: RecyclerView
    private val groupTitle: TextView
    private val adapter: MovieAdapter

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_group, parent, false))
        groupTitle = findViewById(R.id.tv_group_type)
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieAdapter(this, viewFactory)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = this@MovieGroupItemViewImpl.adapter
        }
    }

    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieCardClicked(movieID)
        }
    }

    override fun onSeeAllClicked() {
        listeners.forEach {
            it.onSeeAllClicked()
        }
    }

    override fun displayMovieGroup(movieGroup: MovieGroup) {
        groupTitle.text = movieGroup.movieGroupType.value.toUpperCase()
        adapter.submitList(movieGroup.movies)
    }
}