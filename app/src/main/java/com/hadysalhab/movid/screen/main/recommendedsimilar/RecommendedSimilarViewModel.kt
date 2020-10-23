package com.hadysalhab.movid.screen.main.recommendedsimilar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.list.FetchRecommendedSimilarListUseCase
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleActions
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleStateManager
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import java.util.*
import javax.inject.Inject

class RecommendedSimilarViewModel
@Inject
constructor(
    private val fetchRecommendedSimilarListUseCase: FetchRecommendedSimilarListUseCase,
    private val listWithToolbarTitleStateManager: ListWithToolbarTitleStateManager,
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient
) : ViewModel(), FetchRecommendedSimilarListUseCase.Listener {
    private var isFirstRender: Boolean = true
    private lateinit var groupType: GroupType
    private lateinit var movieName: String
    private var movieId: Int = 0
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
        fetchRecommendedSimilarListUseCase.registerListener(this)
    }

    fun onStart(groupType: GroupType, movieName: String, movieID: Int) {
        if (isFirstRender) {
            dispatch(
                ListWithToolbarTitleActions.SetTitle(
                    "$movieName (${
                        groupType.getFormattedValue()

                    })".toUpperCase(Locale.ROOT)
                )
            )
            isFirstRender = false
            this.groupType = groupType
            this.movieName = movieName
            this.movieId = movieID
            dispatch(ListWithToolbarTitleActions.Request)
            fetchApi(1)
        }
    }

    private fun fetchApi(page: Int) {
        fetchRecommendedSimilarListUseCase.fetchRecommendedSimilarMoviesUseCase(
            groupType = groupType,
            page = page,
            movieId = this.movieId
        )
    }

    //User Interactions-----------------------------------------------------------------------------

    fun onRetryClicked() {
        dispatch(ListWithToolbarTitleActions.Request)
        fetchApi(1)
    }

    fun loadMore() {
        if (fetchRecommendedSimilarListUseCase.isBusy || this.moviesResponse.page + 1 > this.moviesResponse.total_pages) {
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
    override fun onRecommendedSimilarMoviesSuccess(movies: MoviesResponse) {
        this.moviesResponse = movies
        moviesList.addAll(movies.movies ?: emptyList())
        dispatch(ListWithToolbarTitleActions.Success(this.moviesList))
    }

    override fun onRecommendedSimilarMoviesFailure(msg: String) {
        if (state.value!!.movieListScreenState.isPaginationLoading) {
            dispatch(ListWithToolbarTitleActions.PaginationError)
        } else {
            dispatch(ListWithToolbarTitleActions.Error(msg))
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchRecommendedSimilarListUseCase.unregisterListener(this)
    }

}