package com.hadysalhab.movid.screen.common.movielist

import android.content.Context
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


class MovieListScreenImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : MovieListScreen(), MovieListAdapter.Listener {
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
            layoutManager = CustomGridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = this@MovieListScreenImpl.adapter
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

    inner class CustomGridLayoutManager(val context: Context, spanCount: Int) :
        GridLayoutManager(context, spanCount) {
        override fun canScrollVertically(): Boolean {
            return paginationProgressBar.visibility == View.GONE && super.canScrollVertically()
        }
    }

    override fun displayMovies(movies: List<Movie>) {
        if (movies.size <= 20) {
            adapter.notifyDataSetChanged()
        }
        adapter.submitList(movies)
    }

    override fun showPaginationIndicator() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    override fun hidePaginationIndicator() {
        paginationProgressBar.visibility = View.GONE
    }

    override fun showLoadingIndicator() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        progressBar.visibility = View.GONE
    }


    override fun displayEmptyListIndicator(msg: String) {
        emptyResultIndicator.text = msg
        emptyResultIndicator.visibility = View.VISIBLE
        paginationProgressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    override fun handleState(movieListScreenState: MovieListScreenState) {
        val isLoading = movieListScreenState.isLoading
        val isPaginationLoading = movieListScreenState.isPaginationLoading
        val movies = movieListScreenState.data
        if (isLoading) {
            showLoadingIndicator()
        } else {
            hideLoadingIndicator()
        }
        if (isPaginationLoading) {

            showPaginationIndicator()
        } else {
            if (paginationProgressBar.visibility == View.VISIBLE) {
                android.os.Handler().postDelayed({
                    hidePaginationIndicator()
                    animateRecyclerViewScroll()
                }, 300)
            } else {
                hidePaginationIndicator()
            }
        }
        displayMovies(movies)
    }

    private fun animateRecyclerViewScroll() {
        recyclerView.suppressLayout(false)
        recyclerView.post {
            recyclerView.smoothScrollBy(0, convertDpToPixel(16, getContext()))
        }
    }

    override fun onMovieItemClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieItemClicked(movieID)
        }
    }
}
