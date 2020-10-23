package com.hadysalhab.movid.screen.main.featuredlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.list.FetchFeaturedListUseCase
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleActions
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleStateManager
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import javax.inject.Inject

class FeaturedListViewModel @Inject constructor(
    private val fetchFeaturedListUseCase: FetchFeaturedListUseCase,
    private val moviesStateManager: MoviesStateManager,
    private val listWithToolbarTitleStateManager: ListWithToolbarTitleStateManager,
    private val dataValidator: DataValidator,
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient
) : ViewModel(), FetchFeaturedListUseCase.Listener {
    private var isFirstRender: Boolean = true

    private lateinit var groupType: GroupType
    private lateinit var region: String
    private lateinit var moviesResponse: MoviesResponse
    private val moviesList = mutableListOf<Movie>()
    private val dispatch = listWithToolbarTitleStateManager::dispatch

    val state: LiveData<ListWithToolbarTitleState> =
        listWithToolbarTitleStateManager.setInitialStateAndReturn(
            ListWithToolbarTitleState(
                title = "",
                showBackArrow = true,
                movieListScreenState = MovieListScreenState(
                    emptyResultsIconDrawable = R.drawable.ic_sad,
                    emptyResultsMessage = "No Results Found"
                )
            )
        )

    init {
        fetchFeaturedListUseCase.registerListener(this)
    }

    fun onStart(groupType: GroupType, region: String) {
        if (isFirstRender) {
            dispatch(ListWithToolbarTitleActions.SetTitle(groupType.getFormattedValue()))
            isFirstRender = false
            this.groupType = groupType
            this.region = region
            val storedMoviesResponse =
                moviesStateManager.getMoviesResponseByGroupType(groupType)
            if (dataValidator.isMoviesResponseValid(
                    storedMoviesResponse,
                    region
                ) && storedMoviesResponse.movies != null
            ) {
                this.moviesResponse = storedMoviesResponse
                this.moviesList.addAll(storedMoviesResponse.movies)
                dispatch(
                    ListWithToolbarTitleActions.Success(
                        this.moviesList
                    )
                )
            } else {
                dispatch(ListWithToolbarTitleActions.Request)
                fetchApi(1)
            }
        }
    }

    private fun fetchApi(page: Int) {
        fetchFeaturedListUseCase.fetchMoviesResponseUseCase(
            groupType = groupType,
            page = page,
            region = this.region
        )
    }

    //User Interactions-----------------------------------------------------------------------------

    fun onRetryClicked() {
        dispatch(ListWithToolbarTitleActions.Request)
        fetchApi(1)
    }

    fun loadMore() {
        if (fetchFeaturedListUseCase.isBusy || this.moviesResponse.page + 1 > this.moviesResponse.total_pages) {
            return
        }
        dispatch(ListWithToolbarTitleActions.Pagination)
        firebaseAnalyticsClient.logPagination(
            this.moviesResponse.tag.name,
            this.moviesResponse.page + 1
        )
        fetchApi(this.moviesResponse.page + 1)
    }

    //UseCaseResults--------------------------------------------------------------------------------

    override fun onFetchMoviesResponseSuccess(movies: MoviesResponse) {
        this.moviesResponse = movies
        moviesList.addAll(movies.movies ?: emptyList())
        dispatch(ListWithToolbarTitleActions.Success(this.moviesList))
    }

    override fun onFetchMoviesResponseFailure(msg: String) {
        if (state.value!!.movieListScreenState.isPaginationLoading) {
            dispatch(ListWithToolbarTitleActions.PaginationError)
        } else {
            dispatch(ListWithToolbarTitleActions.Error(msg))
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchFeaturedListUseCase.unregisterListener(this)
    }

}