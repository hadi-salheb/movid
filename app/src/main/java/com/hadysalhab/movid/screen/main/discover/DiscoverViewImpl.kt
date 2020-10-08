package com.hadysalhab.movid.screen.main.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movielist.MovieListScreen
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.hadysalhab.movid.screen.main.search.Genre

class DiscoverViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : DiscoverView(),
    MovieListScreen.Listener {
    private val toolbar: Toolbar
    private val movieListFramePlaceholder: FrameLayout
    private val movieListScreen: MovieListScreen
    private val menuToolbarLayout: MenuToolbarLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_discover, parent, false))
        toolbar = findViewById(R.id.toolbar)
        movieListFramePlaceholder = findViewById(R.id.discover_movie_list_placeholder)
        movieListScreen = viewFactory.getMovieScreen(movieListFramePlaceholder)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        menuToolbarLayout.setOverflowMenuIcon(R.drawable.ic_filter)
        toolbar.addView(menuToolbarLayout.getRootView())
        movieListFramePlaceholder.addView(movieListScreen.getRootView())
        movieListScreen.registerListener(this)
    }


    override fun displayLoadingIndicator() {
        movieListScreen.showLoadingIndicator()
    }

    override fun displayPaginationLoading() {
        movieListScreen.showPaginationIndicator()
    }

    override fun displayMovies(movies: List<Movie>) {
        movieListScreen.displayMovies(movies)
    }

    override fun displayEmptyListIndicator(msg: String) {
        movieListScreen.displayEmptyListIndicator(msg)
    }

    override fun setGenreDetail(genre: Genre) {
        menuToolbarLayout.setToolbarTitle(genre.genre)
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