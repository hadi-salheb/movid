package com.hadysalhab.movid.screen.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movielist.MovieListScreen
import com.mancj.materialsearchbar.MaterialSearchBar


class SearchViewImpl(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    SearchView(), MaterialSearchBar.OnSearchActionListener, MovieListScreen.Listener,
    GenreList.Listener {
    private val materialSearchBar: MaterialSearchBar
    private val framePlaceHolder: FrameLayout
    private val movieListScreen: MovieListScreen
    private val genreView: GenreList

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_search, parent, false))
        framePlaceHolder = findViewById(R.id.frame_placeholder)
        materialSearchBar = findViewById(R.id.searchBar)
        materialSearchBar.setOnSearchActionListener(this)
        movieListScreen = viewFactory.getMovieScreen(framePlaceHolder)
        genreView = viewFactory.getGenreList(framePlaceHolder)
        renderMovies()
    }

    override fun onSearchStateChanged(enabled: Boolean) {

    }

    override fun onSearchConfirmed(text: CharSequence?) {
        renderMovies()
        text?.let { text ->
            listeners.forEach { listener ->
                listener.onSearchConfirmed(text)
            }
        }
    }

    override fun onButtonClicked(buttonCode: Int) {
        when (buttonCode) {
            MaterialSearchBar.BUTTON_BACK -> {
                listeners.forEach {
                    it.onSearchBackBtnClick()
                }
            }
        }
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

    override fun displayLoadingIndicator() {
        movieListScreen.showLoadingIndicator()
    }

    override fun displayPaginationLoading() {
        movieListScreen.hidePaginationIndicator()
    }

    override fun displayMovies(movies: List<Movie>) {
        movieListScreen.displayMovies(movies)
    }

    override fun renderGenres() {
        framePlaceHolder.removeAllViews()
        movieListScreen.unregisterListener(this)
        framePlaceHolder.addView(genreView.getRootView())
        genreView.registerListener(this)
    }

    override fun renderMovies() {
        framePlaceHolder.removeAllViews()
        framePlaceHolder.addView(movieListScreen.getRootView())
        movieListScreen.registerListener(this)
        genreView.unregisterListener(this)
    }

    override fun displayEmptyListIndicator(msg: String) {
        movieListScreen.displayEmptyListIndicator(msg)
    }

    override fun onGenreListItemClick(genre: Genre) {
        listeners.forEach {
            it.onGenreListItemClick(genre)
        }
    }
}