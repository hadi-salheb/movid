package com.hadysalhab.movid.screen.common.listtitletoolbar

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movielist.MovieListScreen
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.toolbar.TitleToolbarLayout

class ListWithToolbarTitleImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) :
    ListWithToolbarTitle(), MovieListScreen.Listener {

    private val favoritesPlaceHolder: FrameLayout
    private val toolbar: Toolbar
    private val titleToolbarLayout: TitleToolbarLayout
    private val movieListScreen: MovieListScreen

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_title_toolbar, parent, false))
        favoritesPlaceHolder = findViewById(R.id.favorites_placeholder)
        toolbar = findViewById(R.id.toolbar)
        titleToolbarLayout = viewFactory.getTitleToolbarLayout(toolbar)
        toolbar.addView(titleToolbarLayout.getRootView())
        movieListScreen = viewFactory.getMovieScreen(favoritesPlaceHolder)
        movieListScreen.registerListener(this)
        favoritesPlaceHolder.addView(movieListScreen.getRootView())

    }


    override fun handleScreenState(screenState: ListWithToolbarTitleState) {
        movieListScreenHandleState(
            MovieListScreenState(
                isLoading = screenState.isLoading,
                isPaginationLoading = screenState.isPaginationLoading,
                data = screenState.data,
                emptyResultsIconDrawable = screenState.emptyResultsIconDrawable,
                emptyResultsMessage = screenState.emptyResultsMessage,
                errorMessage = screenState.error,
                paginationError = screenState.isPaginationError
            )
        )

        setToolbarTitle(
            screenState.title
        )
    }

    private fun movieListScreenHandleState(movieListScreenState: MovieListScreenState) {
        movieListScreen.handleState(
            movieListScreenState
        )
    }

    private fun setToolbarTitle(groupType: String) {
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

    override fun onPaginationErrorClicked() {
        listeners.forEach {
            it.onPaginationErrorClicked()
        }
    }
}