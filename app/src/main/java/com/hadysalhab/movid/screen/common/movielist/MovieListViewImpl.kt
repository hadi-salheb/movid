package com.hadysalhab.movid.screen.common.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.convertDpToPixel
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.scrolllistener.OnVerticalScrollListener


class MovieListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : MovieListView(), MovieListAdapter.Listener {
    private val recyclerView: RecyclerView
    private val adapter: MovieListAdapter
    private val progressBar: ProgressBar
    private val paginationProgressBar: ProgressBar
    private val emptyResultIndicator: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_data, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        emptyResultIndicator = findViewById(R.id.empty_result_tv)
        adapter = MovieListAdapter(this, viewFactory)
        progressBar = findViewById(R.id.loading_indicator)
        paginationProgressBar = findViewById(R.id.pagination_loading_indicator)
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = this@MovieListViewImpl.adapter
        }
        recyclerView.apply {
            addOnScrollListener(object : OnVerticalScrollListener() {
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
            })
            setPadding(0, 0, 0, 0)
        }
    }

    override fun displayMovies(movies: List<Movie>) {
        if (movies.size <= 20) {
            adapter.notifyDataSetChanged()
        }
        adapter.submitList(movies)
        emptyResultIndicator.visibility = View.GONE
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        if (paginationProgressBar.visibility == View.VISIBLE) {
            android.os.Handler().postDelayed({
                paginationProgressBar.visibility = View.GONE
                recyclerView.suppressLayout(false)
                recyclerView.post {
                    recyclerView.smoothScrollBy(0, convertDpToPixel(16, getContext()))
                }
            }, 300)
        }
    }

    override fun displayPaginationLoading() {
        emptyResultIndicator.visibility = View.GONE
        paginationProgressBar.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        recyclerView.suppressLayout(true)
    }

    override fun displayLoadingIndicator() {
        emptyResultIndicator.visibility = View.GONE
        paginationProgressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun displayEmptyListIndicator(msg: String) {
        emptyResultIndicator.text = msg
        emptyResultIndicator.visibility = View.VISIBLE
        paginationProgressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    override fun onMovieItemClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieItemClicked(movieID)
        }
    }
}
