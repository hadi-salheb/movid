package com.hadysalhab.movid.screen.main.featuredlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreen
import com.hadysalhab.movid.screen.common.movielist.MovieListScreen
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.toolbar.TitleToolbarLayout
import java.util.*

class FeaturedListScreenImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) :
    FeaturedListScreen(), MovieListScreen.Listener {

    private val errorScreenPlaceholder: FrameLayout
    private val featuredListPlaceHolder: FrameLayout
    private val toolbar: Toolbar
    private val titleToolbarLayout: TitleToolbarLayout
    private val movieListScreen: MovieListScreen
    private val errorScreen: ErrorScreen

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_featured_list, parent, false))
        errorScreenPlaceholder = findViewById(R.id.featured_list_error_placeholder)
        featuredListPlaceHolder = findViewById(R.id.featured_list_placeholder)
        toolbar = findViewById(R.id.toolbar)
        titleToolbarLayout = viewFactory.getTitleToolbarLayout(toolbar)
        toolbar.addView(titleToolbarLayout.getRootView())
        movieListScreen = viewFactory.getMovieScreen(featuredListPlaceHolder)
        movieListScreen.registerListener(this)
        featuredListPlaceHolder.addView(movieListScreen.getRootView())
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceholder)
        errorScreenPlaceholder.addView(errorScreen.getRootView())
    }

    override fun handleScreenState(screenState: FeaturedListScreenState) {
        movieListScreenHandleState(
            MovieListScreenState(
                isLoading = screenState.isLoading,
                isPaginationLoading = screenState.isPaginationLoading,
                data = screenState.data
            )
        )
        if (screenState.error != null) {
            showErrorScreen(screenState.error)
        } else {
            hideErrorScreen()
        }
        displayGroupType(
            screenState.groupType.toUpperCase(Locale.ROOT).split("_").joinToString(" ")
        )
    }

    override fun showErrorScreen(errorMessage: String) {
        errorScreenPlaceholder.visibility = View.VISIBLE
    }

    override fun hideErrorScreen() {
        errorScreenPlaceholder.visibility = View.GONE
    }

    override fun movieListScreenHandleState(movieListScreenState: MovieListScreenState) {
        movieListScreen.handleState(
            movieListScreenState
        )
    }

    override fun displayGroupType(groupType: String) {
        titleToolbarLayout.setToolbarTitle(groupType)
    }

    override fun onMovieItemClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieItemClicked(movieID)
        }
    }

    override fun loadMoreItems() {
        listeners.forEach {
            it.loadMoreItems()
        }
    }
}