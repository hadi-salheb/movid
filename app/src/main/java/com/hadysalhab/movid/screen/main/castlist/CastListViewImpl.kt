package com.hadysalhab.movid.screen.main.castlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Cast
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResults
import com.hadysalhab.movid.screen.common.emptyresults.EmptyResultsState
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreen
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout

class CastListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : CastListView(), MenuToolbarLayout.Listener, CastListAdapter.Listener, ErrorScreen.Listener {
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout
    private val recyclerView: RecyclerView
    private val adapter: CastListAdapter

    //empty results
    private val emptyResultPlaceholder: FrameLayout
    private val emptyResults: EmptyResults

    //error screen
    private val errorScreenPlaceholder: FrameLayout
    private val errorScreen: ErrorScreen

    //loading
    private val progressBar: ProgressBar

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_cast_list, parent, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        setupToolbar()

        //data
        adapter = CastListAdapter(this, viewFactory)
        recyclerView = findViewById(R.id.cast_recyclerview)
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            setHasFixedSize(true)
            adapter = this@CastListViewImpl.adapter
        }

        //empty results
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        emptyResults = viewFactory.getEmptyResults(emptyResultPlaceholder)
        emptyResultPlaceholder.addView(emptyResults.getRootView())

        //error screen
        errorScreenPlaceholder = findViewById(R.id.error_screen_placeholder)
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceholder)
        errorScreenPlaceholder.addView(errorScreen.getRootView())
        errorScreen.registerListener(this)

        //loading
        progressBar = findViewById(R.id.loading_indicator)
    }

    private fun setupToolbar() {
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.showBackArrow()
        menuToolbarLayout.registerListener(this)
    }

    override fun onOverflowMenuIconClick() {

    }

    override fun onBackArrowClicked() {
        listeners.forEach { it.onBackArrowClicked() }
    }

    override fun onRetryClicked() {
        listeners.forEach {
            it.onErrorRetryClicked()
        }
    }

    override fun handleState(castListViewState: CastListViewState) {
        with(castListViewState) {
            menuToolbarLayout.setToolbarTitle(title)
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
            showData(data)
        }
    }

    override fun showLoadingIndicator() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        progressBar.visibility = View.GONE
    }

    override fun showEmptyDataScreen(icon: Int, msg: String) {
        emptyResultPlaceholder.visibility = View.VISIBLE
        emptyResults.render(
            EmptyResultsState(
                icon,
                msg
            )
        )
    }

    override fun hideEmptyDataScreen() {
        emptyResultPlaceholder.visibility = View.GONE
    }

    override fun showData(data: List<Cast>) {
        adapter.submitList(data)
    }

    override fun showErrorScreen(msg: String) {
        errorScreen.displayErrorMessage(msg)
        errorScreenPlaceholder.visibility = View.VISIBLE
    }

    override fun hideErrorScreen() {
        errorScreenPlaceholder.visibility = View.GONE
    }
}