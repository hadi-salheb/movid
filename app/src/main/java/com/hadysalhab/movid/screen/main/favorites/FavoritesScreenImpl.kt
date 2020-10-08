package com.hadysalhab.movid.screen.main.favorites

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

class FavoritesScreenImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) :
    FavoritesScreen(), MovieListScreen.Listener, ErrorScreen.Listener {

    private val errorScreenPlaceholder: FrameLayout
    private val favoritesPlaceHolder: FrameLayout
    private val toolbar: Toolbar
    private val titleToolbarLayout: TitleToolbarLayout
    private val movieListScreen: MovieListScreen
    private val errorScreen: ErrorScreen

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_favorites, parent, false))
        errorScreenPlaceholder = findViewById(R.id.favorites_error_placeholder)
        favoritesPlaceHolder = findViewById(R.id.favorites_placeholder)
        toolbar = findViewById(R.id.toolbar)
        titleToolbarLayout = viewFactory.getTitleToolbarLayout(toolbar)
        toolbar.addView(titleToolbarLayout.getRootView())
        movieListScreen = viewFactory.getMovieScreen(favoritesPlaceHolder)
        movieListScreen.registerListener(this)
        favoritesPlaceHolder.addView(movieListScreen.getRootView())
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceholder)
        errorScreenPlaceholder.addView(errorScreen.getRootView())
        errorScreen.registerListener(this)
    }

    override fun handleScreenState(screenState: FavoritesScreenState) {
        movieListScreenHandleState(
            MovieListScreenState(
                isLoading = screenState.isLoading,
                isPaginationLoading = screenState.isPaginationLoading,
                data = screenState.data
            )
        )
        if (screenState.error != null) {
            errorScreen.displayErrorMessage(screenState.error)
            showErrorScreen(screenState.error)
        } else {
            hideErrorScreen()
        }
        setToolbarTitle(
            "FAVORITES"
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

    override fun setToolbarTitle(groupType: String) {
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

    override fun onRetryClicked() {
        listeners.forEach {
            it.onRetryClicked()
        }
    }
}