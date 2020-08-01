package com.hadysalhab.movid.screen.main.featured

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.screen.common.ViewFactory

class FeaturedViewImpl(inflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    FeaturedView(), MovieGroupAdapter.Listener {
    private val recyclerView: RecyclerView
    private val adapter: MovieGroupAdapter
    private val circularProgress: ProgressBar

    init {
        setRootView(inflater.inflate(R.layout.layout_featured, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieGroupAdapter(this, viewFactory)
        circularProgress = findViewById(R.id.progress_circular)
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@FeaturedViewImpl.adapter
        }
    }

    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieCardClicked(movieID)
        }
    }

    override fun onSeeMoreClicked(movieGroupType: MovieGroupType) {
        listeners.forEach {
            it.onSeeAllClicked(movieGroupType)
        }
    }

    override fun displayMovieGroups(movieGroups: List<MovieGroup>) {
        circularProgress.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.submitList(movieGroups)
    }

    override fun displayLoadingScreen() {
        circularProgress.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

}