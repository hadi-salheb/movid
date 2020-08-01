package com.hadysalhab.movid.screen.main.featured

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movies.MovieAdapter

class MovieGroupItemViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) :
    MovieGroupItemView(), MovieAdapter.Listener {
    private val recyclerView: RecyclerView
    private val groupTitle: TextView
    private val adapter: MovieAdapter
    private lateinit var movieGroupType: MovieGroupType

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_group, parent, false))
        groupTitle = findViewById(R.id.tv_group_type)
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieAdapter(
            this,
            viewFactory
        )
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
            it.onSeeAllClicked(this.movieGroupType)
        }
    }


    override fun displayMovieGroup(movieGroup: MovieGroup) {
        movieGroupType = movieGroup.movieGroupType
        groupTitle.text = movieGroupType.value.toUpperCase().split("_").joinToString(" ")
        adapter.submitList(movieGroup.movies)
    }
}