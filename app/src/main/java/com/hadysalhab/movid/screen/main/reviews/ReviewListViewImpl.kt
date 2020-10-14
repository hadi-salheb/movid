package com.hadysalhab.movid.screen.main.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.convertDpToPixel
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResults
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResultsState
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreen
import com.hadysalhab.movid.screen.common.scrolllistener.OnVerticalScrollListener


class ReviewListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : ReviewListView(), ErrorScreen.Listener, ReviewListAdapter.Listener {
    private val errorScreenPlaceholder: FrameLayout
    private val errorScreen: ErrorScreen
    private val recyclerView: RecyclerView
    private val adapter: ReviewListAdapter
    private val progressBar: ProgressBar
    private val emptyResultPlaceholder: FrameLayout
    private val emptyResults: EmptyResults

    private var isLoading = false
    private var isError = false

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_data, parent, false))

        //error screen
        errorScreenPlaceholder = findViewById(R.id.error_screen_placeholder)
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceholder)
        errorScreenPlaceholder.addView(errorScreen.getRootView())
        errorScreen.registerListener(this)


        //empty results
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        emptyResults = viewFactory.getEmptyResults(emptyResultPlaceholder)
        emptyResultPlaceholder.addView(emptyResults.getRootView())

        //data
        adapter = ReviewListAdapter(this, viewFactory)
        recyclerView = findViewById(R.id.rv_data)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@ReviewListViewImpl.adapter
            addOnScrollListener(object : OnVerticalScrollListener() {
                override fun onScrolledUp() {

                }

                override fun onScrolledDown() {
                }

                override fun onScrolledToTop() {
                }

                override fun onScrolledToBottom() {
                    listeners.forEach {
                        if (!this@ReviewListViewImpl.isLoading && !this@ReviewListViewImpl.isError) {
                            it.loadMoreItems()
                        }
                    }
                }
            }
            )
        }
        //loading
        progressBar = findViewById(R.id.loading_indicator)
    }

    override fun handleState(reviewListState: ReviewListState) {
        with(reviewListState) {
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

    override fun showData(data: List<Review>) {
        adapter.submitList(data)
        this@ReviewListViewImpl.isError = false
        this@ReviewListViewImpl.isLoading = false
    }

    override fun showPagination(data: List<Review>) {
        adapter.submitList(
            data + Review(
                "-1000",
                "LOADING",
                "LOADING",
                "LOADING"
            )
        )
        animateRecyclerViewScroll()
        this@ReviewListViewImpl.isLoading = true
        this@ReviewListViewImpl.isError = false
    }

    override fun showPaginationError(data: List<Review>) {
        adapter.submitList(
            data + Review(
                "-1000",
                "ERROR",
                "ERROR",
                "ERROR"
            )
        )
        this@ReviewListViewImpl.isError = true
        this@ReviewListViewImpl.isLoading = false
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

    override fun onRetryClicked() {
        listeners.forEach {
            it.onRetryClicked()
        }
    }

    override fun onPaginationErrorClicked() {
        listeners.forEach {
            it.onPaginationErrorClicked()
        }
    }

}
