package com.hadysalhab.movid.screen.common.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.convertDpToPixel
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResults
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResultsState
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreen
import com.hadysalhab.movid.screen.common.scrolllistener.OnVerticalScrollListener


class MovieListScreenImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : MovieListScreen(), MovieListAdapter.Listener, ErrorScreen.Listener {
    private val errorScreenPlaceholder: FrameLayout
    private val errorScreen: ErrorScreen
    private val recyclerView: RecyclerView
    private val adapter: MovieListAdapter
    private val progressBar: ProgressBar
    private val emptyResultPlaceholder: FrameLayout
    private val emptyResults: EmptyResults

    private var isLoading = false
    private var isError = false

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_data, parent, false))
        errorScreenPlaceholder = findViewById(R.id.error_screen_placeholder)
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceholder)
        errorScreenPlaceholder.addView(errorScreen.getRootView())
        errorScreen.registerListener(this)
        recyclerView = findViewById(R.id.rv_movies)
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        emptyResults = viewFactory.getEmptyResults(emptyResultPlaceholder)
        emptyResultPlaceholder.addView(emptyResults.getRootView())
        adapter = MovieListAdapter(this, viewFactory)
        progressBar = findViewById(R.id.loading_indicator)
        recyclerView.apply {
            val gridManager = GridLayoutManager(context, 3)
            gridManager.spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (this@MovieListScreenImpl.adapter.getItemViewType(position) == this@MovieListScreenImpl.adapter.LOADING || this@MovieListScreenImpl.adapter.getItemViewType(
                            position
                        ) == this@MovieListScreenImpl.adapter.ERROR
                    ) {
                        3
                    } else {
                        1
                    }
                }

            })
            layoutManager = gridManager
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
                        if (!this@MovieListScreenImpl.isLoading && !this@MovieListScreenImpl.isError) {
                            it.loadMoreItems()
                        }
                    }
                }
            })
            setPadding(0, 0, 0, 0)
        }
    }

    override fun displayMovies(movies: List<Movie>) {
        adapter.submitList(movies)
    }

    override fun showPaginationIndicator() {
        recyclerView.clipToPadding = false
        recyclerView.setPadding(0, 0, 0, convertDpToPixel(150, getContext()))
    }

    override fun hidePaginationIndicator() {
        recyclerView.setPadding(0, 0, 0, 0)
    }

    override fun showLoadingIndicator() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        progressBar.visibility = View.GONE
    }


    override fun handleState(movieListScreenState: MovieListScreenState) {
        with(movieListScreenState) {
            if (isLoading) {
                showLoadingIndicator()
            } else {
                hideLoadingIndicator()
            }
            if (!isLoading && errorMessage == null && data.isEmpty()) {
                emptyResultPlaceholder.visibility = View.VISIBLE
                emptyResults.render(
                    EmptyResultsState(
                        emptyResultsIconDrawable,
                        emptyResultsMessage
                    )
                )
            } else {
                emptyResultPlaceholder.visibility = View.GONE
            }
            if (errorMessage != null) {
                errorScreen.displayErrorMessage(errorMessage)
                showErrorScreen(errorMessage)
            } else {
                hideErrorScreen()
            }
            when {
                isPaginationLoading -> {
                    adapter.submitList(
                        data + Movie(
                            -1000,
                            "LOADING",
                            "LOADING",
                            "LOADING",
                            -1000.0,
                            -1000,
                            "LOADING",
                            "LOADING"
                        )
                    )
                    animateRecyclerViewScroll()
                    this@MovieListScreenImpl.isLoading = true
                    this@MovieListScreenImpl.isError = false
                }
                paginationError -> {
                    adapter.submitList(
                        data + Movie(
                            -1000,
                            "ERROR",
                            "ERROR",
                            "ERROR",
                            -1000.0,
                            -1000,
                            "ERROR",
                            "ERROR"
                        )
                    )
                    this@MovieListScreenImpl.isError = true
                    this@MovieListScreenImpl.isLoading = false
                }
                else -> {
                    adapter.submitList(data)
                    this@MovieListScreenImpl.isError = false
                    this@MovieListScreenImpl.isLoading = false
                }
            }
        }
    }

    private fun showErrorScreen(errorMessage: String) {
        errorScreenPlaceholder.visibility = View.VISIBLE
    }

    private fun hideErrorScreen() {
        errorScreenPlaceholder.visibility = View.GONE
    }

    private fun animateRecyclerViewScroll() {
        recyclerView.suppressLayout(false)
        recyclerView.post {
            recyclerView.smoothScrollBy(0, convertDpToPixel(70, getContext()))
        }
    }

    override fun onMovieItemClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieItemClicked(movieID)
        }
    }

    override fun onPaginationErrorClicked() {
        listeners.forEach {
            it.onPaginationErrorClicked()
        }
    }

    override fun onRetryClicked() {
        listeners.forEach {
            it.onRetryClicked()
        }
    }
}
