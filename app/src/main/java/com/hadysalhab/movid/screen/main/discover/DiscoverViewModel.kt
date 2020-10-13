package com.hadysalhab.movid.screen.main.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.discover.DiscoverMoviesUseCase
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleActions
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleStateManager
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.main.search.Genre
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    private val listWithToolbarTitleStateManager: ListWithToolbarTitleStateManager
) : ViewModel(), DiscoverMoviesUseCase.Listener {
    private var isFirstRender: Boolean = true
    private lateinit var genre: Genre
    private lateinit var moviesResponse: MoviesResponse
    private val moviesList = mutableListOf<Movie>()
    private val dispatch = listWithToolbarTitleStateManager::dispatch

    val state: LiveData<ListWithToolbarTitleState> =
        listWithToolbarTitleStateManager.setInitialStateAndReturn(
            ListWithToolbarTitleState(
                movieListScreenState = MovieListScreenState(
                    emptyResultsIconDrawable = R.drawable.ic_sad,
                    emptyResultsMessage = "No Results Found"
                ),
                title = "",
                menuIcon = R.drawable.ic_filter
            )
        )


    init {
        discoverMoviesUseCase.registerListener(this)
    }

    fun onStart(genre: Genre) {
        if (isFirstRender) {
            this.genre = genre
            dispatch(
                ListWithToolbarTitleActions.SetTitle(
                    genre.getFormattedName()
                )
            )
            isFirstRender = false
            dispatch(ListWithToolbarTitleActions.Request)
            fetchApi(1)
        }
    }

    private fun fetchApi(page: Int) {
        discoverMoviesUseCase.discoverMoviesUseCase(
            page,
            genreIds = this.genre.id.toString()
        )
    }

    //User Interactions-----------------------------------------------------------------------------
    fun onRetryClicked() {
        dispatch(ListWithToolbarTitleActions.Request)
        fetchApi(1)
    }

    fun loadMore() {
        if (discoverMoviesUseCase.isBusy || this.moviesResponse.page + 1 > this.moviesResponse.total_pages) {
            return
        }
        dispatch(ListWithToolbarTitleActions.Pagination)
        fetchApi(this.moviesResponse.page + 1)
    }

    //UseCaseResults--------------------------------------------------------------------------------
    override fun onFetchDiscoverMoviesSuccess(movies: MoviesResponse) {
        this.moviesResponse = movies
        moviesList.addAll(movies.movies ?: emptyList())
        dispatch(ListWithToolbarTitleActions.Success(this.moviesList))
    }

    override fun onFetchDiscoverMoviesFailure(msg: String) {
        if (state.value!!.movieListScreenState.isPaginationLoading) {
            dispatch(ListWithToolbarTitleActions.PaginationError)
        } else {
            dispatch(ListWithToolbarTitleActions.Error(msg))
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        discoverMoviesUseCase.unregisterListener(this)
    }

}