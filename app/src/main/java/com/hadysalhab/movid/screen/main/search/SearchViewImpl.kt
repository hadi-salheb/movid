package com.hadysalhab.movid.screen.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hadysalhab.movid.R
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
    private lateinit var screenState: SearchScreenState

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_search, parent, false))
        framePlaceHolder = findViewById(R.id.search_data_placeholder)
        materialSearchBar = findViewById(R.id.searchBar)
        materialSearchBar.setOnSearchActionListener(this)
        movieListScreen = viewFactory.getMovieScreen(framePlaceHolder)
        genreView = viewFactory.getGenreList(framePlaceHolder)
    }

    //Callbacks-------------------------------------------------------------------------------------
    override fun onSearchStateChanged(enabled: Boolean) {
        if (!enabled) {
            listeners.forEach {
                it.onSearchBackBtnClick()
            }
        }
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        listeners.forEach {
            it.onSearchConfirmed(text.toString())
        }
    }

    override fun onButtonClicked(buttonCode: Int) {

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

    override fun onGenreListItemClick(genre: Genre) {
        listeners.forEach {
            it.onGenreListItemClick(genre)
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun handleState(searchScreenState: SearchScreenState) {
        if (this::screenState.isInitialized) {
            if (this.screenState.movieListScreenState != null && searchScreenState.movieListScreenState == null) {
                showGenreList()
            } else if (this.screenState.movieListScreenState == null && searchScreenState.movieListScreenState != null) {
                showMovieListScreen()
            }
        } else {
            with(searchScreenState) {
                if (movieListScreenState == null) {
                    showGenreList()
                } else {
                    showMovieListScreen()
                }
            }
        }
        searchScreenState.movieListScreenState?.let {
            movieListScreen.handleState(it)
        }
        this.screenState = searchScreenState
    }

    private fun showGenreList() {
        framePlaceHolder.removeAllViews()
        movieListScreen.unregisterListener(this@SearchViewImpl)
        genreView.registerListener(this@SearchViewImpl)
        framePlaceHolder.addView(genreView.getRootView())
    }

    private fun showMovieListScreen() {
        framePlaceHolder.removeAllViews()
        framePlaceHolder.addView(movieListScreen.getRootView())
        movieListScreen.registerListener(this@SearchViewImpl)
        genreView.unregisterListener(this@SearchViewImpl)
    }
}