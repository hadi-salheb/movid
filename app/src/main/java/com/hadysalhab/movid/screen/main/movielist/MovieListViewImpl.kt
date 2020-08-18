package com.hadysalhab.movid.screen.main.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory

class MovieListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : MovieListView(), MovieListAdapter.Listener {
    private val recyclerView: RecyclerView
    private val adapter: MovieListAdapter

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_movie_list, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieListAdapter(this, viewFactory)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@MovieListViewImpl.adapter
        }
    }

    override fun displayMovies(movies: List<Movie>) {
        adapter.submitList(movies)
    }

    override fun onMovieItemClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieItemClicked(movieID)
        }
    }
}