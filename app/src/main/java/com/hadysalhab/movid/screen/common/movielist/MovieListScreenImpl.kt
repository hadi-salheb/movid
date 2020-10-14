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

    private val gridLayoutManager: GridLayoutManager

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_data, parent, false))
        errorScreenPlaceholder = findViewById(R.id.error_screen_placeholder)
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceholder)
        errorScreenPlaceholder.addView(errorScreen.getRootView())
        errorScreen.registerListener(this)
        recyclerView = findViewById(R.id.rv_data)
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        emptyResults = viewFactory.getEmptyResults(emptyResultPlaceholder)
        emptyResultPlaceholder.addView(emptyResults.getRootView())
        adapter = MovieListAdapter(this, viewFactory)
        progressBar = findViewById(R.id.loading_indicator)
        gridLayoutManager = GridLayoutManager(getContext(), 3)
        recyclerView.apply {
            gridLayoutManager.spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
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
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
            adapter = this@MovieListScreenImpl.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val visibleItemCount = gridLayoutManager.childCount
                        val totalItemCount = gridLayoutManager.itemCount
                        val pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition()
                        if (!this@MovieListScreenImpl.isLoading && !this@MovieListScreenImpl.isError && ((visibleItemCount + pastVisibleItems) >= totalItemCount)) {
                            listeners.forEach {
                                it.loadMoreItems()
                            }
                        }
                    }
                }
            })
        }
    }

    override fun handleState(movieListScreenState: MovieListScreenState) {
        with(movieListScreenState) {
            if (isLoading) {
                showLoadingIndicator()
            } else {
                hideLoadingIndicator()
            }
            if (!isLoading && errorMessage == null && data.isEmpty()) {
                showEmptyDataScreen(emptyResultsIconDrawable, emptyResultsMessage)
            } else {
                hideEmptyDataScreen()
            }
            if (errorMessage != null) {
                showErrorScreen(errorMessage)
            } else {
                hideErrorScreen()
            }
            when {
                isPaginationLoading -> {
                    showPagination(data)
                }
                paginationError -> {
                    showPaginationError(data)
                }
                else -> {
                    showData(data)
                }
            }
        }
    }

    //Helper Functions------------------------------------------------------------------------------

    override fun showEmptyDataScreen(icon: Int, msg: String) {
        emptyResultPlaceholder.visibility = View.VISIBLE
        emptyResults.render(
            EmptyResultsState(
                icon,
                msg
            )
        )
    }

    override fun showData(data: List<Movie>) {
        adapter.submitList(data)
        this@MovieListScreenImpl.isError = false
        this@MovieListScreenImpl.isLoading = false
    }

    override fun showPagination(data: List<Movie>) {
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
        this@MovieListScreenImpl.isLoading = true
        this@MovieListScreenImpl.isError = false
    }

    override fun showPaginationError(data: List<Movie>) {
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

    override fun hideEmptyDataScreen() {
        emptyResultPlaceholder.visibility = View.GONE
    }

    override fun showErrorScreen(msg: String) {
        errorScreen.displayErrorMessage(msg)
        errorScreenPlaceholder.visibility = View.VISIBLE
    }

    override fun hideErrorScreen() {
        errorScreenPlaceholder.visibility = View.GONE
    }


    override fun showLoadingIndicator() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        progressBar.visibility = View.GONE
    }

    private fun animateRecyclerViewScroll() {
        recyclerView.suppressLayout(false)
        recyclerView.post {
            recyclerView.smoothScrollBy(0, convertDpToPixel(70, getContext()))
        }
    }


    //Callbacks-------------------------------------------------------------------------------------
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
