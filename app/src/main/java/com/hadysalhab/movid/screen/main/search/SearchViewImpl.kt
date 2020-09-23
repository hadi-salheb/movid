package com.hadysalhab.movid.screen.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movielist.MovieListView
import com.mancj.materialsearchbar.MaterialSearchBar


class SearchViewImpl(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    SearchView(), MaterialSearchBar.OnSearchActionListener, MovieListView.Listener,
    GenreList.Listener {
    private val materialSearchBar: MaterialSearchBar
    private val framePlaceHolder: FrameLayout
    private val movieListView: MovieListView
    private val genreView: GenreList

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_search, parent, false))
        framePlaceHolder = findViewById(R.id.frame_placeholder)
        materialSearchBar = findViewById(R.id.searchBar)
        materialSearchBar.setOnSearchActionListener(this)
        movieListView = viewFactory.getMovieListView(framePlaceHolder)
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
        movieListView.displayLoadingIndicator()
    }

    override fun displayPaginationLoading() {
        movieListView.displayPaginationLoading()
    }

    override fun displayMovies(movies: List<Movie>) {
        movieListView.displayMovies(movies)
    }

    override fun renderGenres() {
        framePlaceHolder.removeAllViews()
        movieListView.unregisterListener(this)
        framePlaceHolder.addView(genreView.getRootView())
        genreView.registerListener(this)
    }

    override fun renderMovies() {
        framePlaceHolder.removeAllViews()
        framePlaceHolder.addView(movieListView.getRootView())
        movieListView.registerListener(this)
        genreView.unregisterListener(this)
    }

    override fun displayEmptyListIndicator(msg: String) {
        movieListView.displayEmptyListIndicator(msg)
    }

    override fun onGenreListItemClick(genre: Genre) {
        listeners.forEach {
            it.onGenreListItemClick(genre)
        }
    }
}