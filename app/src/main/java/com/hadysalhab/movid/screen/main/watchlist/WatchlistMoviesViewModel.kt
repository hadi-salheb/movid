package com.hadysalhab.movid.screen.main.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.MAX_NUMBER_OF_DATA_PER_PAGE
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.watchlist.FetchWatchlistMoviesUseCase
import com.hadysalhab.movid.screen.common.events.MovieDetailEvents
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleActions
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleStateManager
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

class WatchlistMoviesViewModel @Inject constructor(
    private val fetchWatchlistMoviesUseCase: FetchWatchlistMoviesUseCase,
    private val listWithToolbarTitleStateManager: ListWithToolbarTitleStateManager,
    private val schemaToModelHelper: SchemaToModelHelper
) : ViewModel(), FetchWatchlistMoviesUseCase.Listener {

    private lateinit var watchlistMovies: MoviesResponse
    private var moviesList = setOf<Movie>()
    val state: LiveData<ListWithToolbarTitleState> =
        listWithToolbarTitleStateManager.setInitialStateAndReturn(
            ListWithToolbarTitleState(
                title = "WATCHLIST",
                movieListScreenState = MovieListScreenState(
                    emptyResultsIconDrawable = R.drawable.ic_watchlist,
                    emptyResultsMessage = "No Movies Added To Watchlist"
                )
            )
        )
    private var isFirstRender = true
    private val dispatch = listWithToolbarTitleStateManager::dispatch

    init {
        EventBus.getDefault().register(this)
        fetchWatchlistMoviesUseCase.registerListener(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onWatchlistEvent(event: MovieDetailEvents) {
        if (!this::watchlistMovies.isInitialized) {
            return
        }
        when (event) {
            is MovieDetailEvents.AddToWatchlist -> {
                watchlistMovies =
                    watchlistMovies.copy(totalResults = watchlistMovies.totalResults + 1)
                val oldMovieSet = this.moviesList
                val newMovieSet = mutableSetOf(
                    schemaToModelHelper.getMovieFromMovieDetail(
                        event.movieDetail
                    )
                )
                newMovieSet.addAll(oldMovieSet) // newSet = [...newAddedMovies + ... oldMovies]
                updateWatchlistMovies()
                moviesList = newMovieSet
                dispatch(
                    ListWithToolbarTitleActions.Success(
                        moviesList.toList()
                    )
                )
            }
            is MovieDetailEvents.RemoveFromWatchlist -> {
                watchlistMovies =
                    watchlistMovies.copy(totalResults = watchlistMovies.totalResults - 1)
                moviesList = moviesList.filter { it.id != event.movieDetail.details.id }.toSet()
                updateWatchlistMovies()
                dispatch(
                    ListWithToolbarTitleActions.Success(
                        moviesList.toList()
                    )
                )
            }
        }

    }

    private fun updateWatchlistMovies() {
        watchlistMovies = watchlistMovies.copy(
            page = max(floor((moviesList.size * 1.0) / MAX_NUMBER_OF_DATA_PER_PAGE), 1.0).toInt(),
            total_pages = ceil(((watchlistMovies.totalResults)) * 1.0 / MAX_NUMBER_OF_DATA_PER_PAGE).toInt()
        )
    }

    fun onStart() {
        if (isFirstRender) {
            isFirstRender = false
            dispatch(ListWithToolbarTitleActions.Request)
            fetchApi(1)
        } else if (this::watchlistMovies.isInitialized) {
            val numberOfDisplayedMovies = moviesList.size
            if (watchlistMovies.page + 1 <= watchlistMovies.total_pages && (numberOfDisplayedMovies < (MAX_NUMBER_OF_DATA_PER_PAGE * (watchlistMovies.page + 1)))) {
                dispatch(ListWithToolbarTitleActions.Request)
                if (watchlistMovies.page == 1) {
                    fetchApi(1)
                }
                fetchApi(this.watchlistMovies.page + 1)
            }
        }
    }

    private fun fetchApi(page: Int) {
        fetchWatchlistMoviesUseCase.fetchWatchlistUseCase(
            page = page
        )
    }

    //UserInteractions------------------------------------------------------------------------------

    fun loadMore() {
        if (fetchWatchlistMoviesUseCase.isBusy || this.watchlistMovies.page + 1 > this.watchlistMovies.total_pages) {
            return
        }
        dispatch(ListWithToolbarTitleActions.Pagination)
        fetchApi(this.watchlistMovies.page + 1)
    }

    fun onRetry() {
        dispatch(ListWithToolbarTitleActions.Request)
        if (this::watchlistMovies.isInitialized) {
            fetchApi(this.watchlistMovies.page)
        } else {
            fetchApi(1)
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchWatchlistMoviesUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }

    //UseCaseResults--------------------------------------------------------------------------------

    override fun onFetchWatchlistMoviesSuccess(movies: MoviesResponse) {
        this.watchlistMovies = movies
        moviesList = moviesList.toMutableSet().apply {
            addAll(movies.movies ?: emptySet())
        }
        dispatch(ListWithToolbarTitleActions.Success(moviesList.toList()))
    }

    override fun onFetchWatchlistMoviesFailure(msg: String) {
        if (state.value!!.movieListScreenState.isPaginationLoading) {
            dispatch(ListWithToolbarTitleActions.PaginationError)
        } else {
            dispatch(ListWithToolbarTitleActions.Error(msg))
        }
    }
    //----------------------------------------------------------------------------------------------
}