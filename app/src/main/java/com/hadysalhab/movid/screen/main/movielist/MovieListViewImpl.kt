package com.hadysalhab.movid.screen.main.movielist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
    private val progressBar: ProgressBar
    private val paginationProgressBar: ProgressBar

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_movie_list, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieListAdapter(this, viewFactory)
        progressBar = findViewById(R.id.loading_indicator)
        paginationProgressBar = findViewById(R.id.pagination_loading_indicator)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@MovieListViewImpl.adapter
        }
        recyclerView.addOnScrollListener(object : OnVerticalScrollListener() {
            override fun onScrolledUp() {

            }

            override fun onScrolledDown() {
            }

            override fun onScrolledToTop() {
            }

            override fun onScrolledToBottom() {
                listeners.forEach {
                    it.loadMoreItems()
                }
            }

        }
        )
    }

    override fun displayMovies(movies: List<Movie>) {
        adapter.submitList(movies)

        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        if (paginationProgressBar.visibility == View.VISIBLE) {
            android.os.Handler().postDelayed({
                paginationProgressBar.visibility = View.GONE
            }, 300)
        }
    }

    override fun displayPaginationLoading() {
        paginationProgressBar.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

    }

    override fun displayLoadingIndicator() {
        paginationProgressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun onMovieItemClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieItemClicked(movieID)
        }
    }

    //  https://stackoverflow.com/questions/26543131/how-to-implement-endless-list-with-recyclerview
    abstract class OnVerticalScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!recyclerView.canScrollVertically(-1)) {
                onScrolledToTop()
            } else if (!recyclerView.canScrollVertically(1)) {
                onScrolledToBottom()
            } else if (dy < 0) {
                onScrolledUp()
            } else if (dy > 0) {
                onScrolledDown()
            }
        }

        abstract fun onScrolledUp()
        abstract fun onScrolledDown()
        abstract fun onScrolledToTop()
        abstract fun onScrolledToBottom()
    }
}
