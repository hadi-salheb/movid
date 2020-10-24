package com.hadysalhab.movid.screen.main.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.movies.FilterStoreState
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.discover.DiscoverMoviesUseCase
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleActions
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleStateManager
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.viewmodels.SavedStateViewModel
import com.hadysalhab.movid.screen.main.search.Genre
import javax.inject.Inject

private const val STORE_FILTER_STATE = "com.hadysalhab.movid.screen.main.discover.store.key"

class DiscoverViewModel @Inject constructor(
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    private val discoverMoviesFilterStateStore: DiscoverMoviesFilterStateStore,
    private val listWithToolbarTitleStateManager: ListWithToolbarTitleStateManager,
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient
) : SavedStateViewModel(), DiscoverMoviesUseCase.Listener {
    private var isFirstRender: Boolean = true
    private lateinit var genre: Genre
    private lateinit var moviesResponse: MoviesResponse
    private val moviesList = mutableListOf<Movie>()
    private val dispatch = listWithToolbarTitleStateManager::dispatch

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var currentStoreFilterState: FilterStoreState

    val state: LiveData<ListWithToolbarTitleState> =
        listWithToolbarTitleStateManager.setInitialStateAndReturn(
            ListWithToolbarTitleState(
                movieListScreenState = MovieListScreenState(
                    emptyResultsIconDrawable = R.drawable.ic_sad,
                    emptyResultsMessage = "No Results Found"
                ),
                title = "",
                showBackArrow = true,
                menuIcon = R.drawable.ic_filter
            )
        )

    override fun init(savedStateHandle: SavedStateHandle) {
        discoverMoviesUseCase.registerListener(this)
        this.savedStateHandle = savedStateHandle
        checkStoreFilterState()
    }

    //Process Death Check
    private fun checkStoreFilterState() {
        this.currentStoreFilterState = if (savedStateHandle.contains(STORE_FILTER_STATE)) {
            val savedStoreState = savedStateHandle.get<FilterStoreState>(STORE_FILTER_STATE)!!
            discoverMoviesFilterStateStore.updateStoreState(savedStoreState)
            savedStoreState
        } else {
            discoverMoviesFilterStateStore.currentFilterState
        }
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
        } else {
            val storeFilterState = discoverMoviesFilterStateStore.currentFilterState
            if (storeFilterState != currentStoreFilterState) {
                currentStoreFilterState = storeFilterState
                this.moviesList.clear()
                dispatch(ListWithToolbarTitleActions.Request)
                fetchApi(1)
            }
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
        firebaseAnalyticsClient.logPagination(this.genre.genreName, this.moviesResponse.page + 1)
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

    fun onSavedInstanceState() {
        savedStateHandle.set(STORE_FILTER_STATE, this.currentStoreFilterState)
    }

}