package com.hadysalhab.movid.screen.main.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.convertDpToPixel
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResults
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResultsState
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreen
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout


class ReviewListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : ReviewListView(), ErrorScreen.Listener, ReviewListAdapter.Listener, MenuToolbarLayout.Listener {
    private val dataPlaceHolder: FrameLayout
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout

    private val errorScreenPlaceholder: FrameLayout
    private val errorScreen: ErrorScreen
    private val recyclerView: RecyclerView
    private val adapter: ReviewListAdapter
    private val progressBar: ProgressBar
    private val emptyResultPlaceholder: FrameLayout
    private val emptyResults: EmptyResults

    private var isLoading = false
    private var isError = false

    private val linearLayoutManager: LinearLayoutManager

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_title_toolbar, parent, false))
        linearLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)


        dataPlaceHolder = findViewById(R.id.data_placeholder)
        val dataLayout = layoutInflater.inflate(R.layout.layout_list_data, dataPlaceHolder, true)
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        toolbar.addView(menuToolbarLayout.getRootView())

        //error screen
        errorScreenPlaceholder = dataLayout.findViewById(R.id.error_screen_placeholder)
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceholder)
        errorScreenPlaceholder.addView(errorScreen.getRootView())
        errorScreen.registerListener(this)


        //empty results
        emptyResultPlaceholder = dataLayout.findViewById(R.id.empty_result_placeholder)
        emptyResults = viewFactory.getEmptyResults(emptyResultPlaceholder)
        emptyResultPlaceholder.addView(emptyResults.getRootView())

        //data
        adapter = ReviewListAdapter(this, viewFactory)
        recyclerView = dataLayout.findViewById(R.id.rv_data)
        recyclerView.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = this@ReviewListViewImpl.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val visibleItemCount = linearLayoutManager.childCount
                        val totalItemCount = linearLayoutManager.itemCount
                        val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                        if (!this@ReviewListViewImpl.isLoading && !this@ReviewListViewImpl.isError && ((visibleItemCount + pastVisibleItems) >= totalItemCount)) {
                            listeners.forEach {
                                it.loadMoreItems()
                            }
                        }
                    }
                }
            }
            )
        }
        //loading
        progressBar = dataLayout.findViewById(R.id.loading_indicator)
    }

    override fun handleState(reviewListState: ReviewListState) {
        with(reviewListState) {
            menuToolbarLayout.setOverflowMenuIcon(null)
            menuToolbarLayout.setToolbarTitle(title)
            menuToolbarLayout.showBackArrow()
            menuToolbarLayout.registerListener(this@ReviewListViewImpl)
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

    override fun onOverflowMenuIconClick() {

    }

    override fun onBackArrowClicked() {
        listeners.forEach {
            it.onBackArrowClicked()
        }
    }

}
