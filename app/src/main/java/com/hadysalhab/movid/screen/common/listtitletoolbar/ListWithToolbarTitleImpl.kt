package com.hadysalhab.movid.screen.common.listtitletoolbar

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.loginrequired.LoginRequiredView
import com.hadysalhab.movid.screen.common.movielist.MovieListScreen
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout

class ListWithToolbarTitleImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) :
    ListWithToolbarTitle(), MovieListScreen.Listener, MenuToolbarLayout.Listener,
    LoginRequiredView.Listener {

    private val favoritesPlaceHolder: FrameLayout
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout
    private val movieListScreen: MovieListScreen
    private val loginRequiredView : LoginRequiredView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_title_toolbar, parent, false))
        favoritesPlaceHolder = findViewById(R.id.data_placeholder)
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        menuToolbarLayout.registerListener(this)
        toolbar.addView(menuToolbarLayout.getRootView())
        movieListScreen = viewFactory.getMovieScreen(favoritesPlaceHolder)
        movieListScreen.registerListener(this)
        favoritesPlaceHolder.addView(movieListScreen.getRootView())
        loginRequiredView = viewFactory.getLoginRequiredView(favoritesPlaceHolder)
    }


    override fun handleScreenState(screenState: ListWithToolbarTitleState) {
        if (screenState.showLoginRequired) {
            favoritesPlaceHolder.removeAllViews()
            this.loginRequiredView.registerListener(this)
            this.loginRequiredView.setText(screenState.loginRequiredText!!)
            favoritesPlaceHolder.addView(this.loginRequiredView.getRootView())
        } else {
            movieListScreenHandleState(
                screenState.movieListScreenState
            )
        }
        with(menuToolbarLayout) {
            setToolbarTitle(
                screenState.title
            )
            setOverflowMenuIcon(
                screenState.menuIcon
            )
            if (screenState.showBackArrow) {
                showBackArrow()
            }
        }
    }

    private fun movieListScreenHandleState(movieListScreenState: MovieListScreenState) {
        movieListScreen.handleState(
            movieListScreenState
        )
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

    override fun onOverflowMenuIconClick() {
        listeners.forEach {
            it.onMenuIconClicked()
        }
    }

    override fun onBackArrowClicked() {
        listeners.forEach {
            it.onBackArrowClick()
        }
    }

    override fun onLoginRequiredBtnClicked() {
        listeners.forEach {
            it.onLoginRequiredBtnClicked()
        }
    }
}