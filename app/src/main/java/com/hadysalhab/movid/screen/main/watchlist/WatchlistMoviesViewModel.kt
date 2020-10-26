package com.hadysalhab.movid.screen.main.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.authentication.SignOutUseCase
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.constants.GUEST_SESSION_ID
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
    private val signOutUseCase: SignOutUseCase,
    private val listWithToolbarTitleStateManager: ListWithToolbarTitleStateManager,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel(), FetchWatchlistMoviesUseCase.Listener {

    private lateinit var watchlistMovies: MoviesResponse
    private val sessionID = sharedPreferencesManager.getStoredSessionId()
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
                removeFromWatchList(event.movie.details.id)
            }
            is MovieDetailEvents.MovieDetailFetched -> {
                //this flow can happen, because tmdb by default remove rated movies from watchlist
                //if user rate a movie,then pull to refresh, the newly updated data will not be part of watchlist
                if (event.movie.accountStates != null) {
                    val isInUserWatchlist = event.movie.accountStates.watchlist
                    val isInCurrentDisplayedWatchlist =
                        this.moviesList.any { it.id == event.movie.details.id }
                    if (isInCurrentDisplayedWatchlist && !isInUserWatchlist) {
                        removeFromWatchList(event.movie.details.id)
                    }
                }
            }
        }

    }

    private fun removeFromWatchList(movieId: Int) {
        watchlistMovies =
            watchlistMovies.copy(totalResults = watchlistMovies.totalResults - 1)
        moviesList = moviesList.filter { it.id != movieId }.toSet()
        updateWatchlistMovies()
        dispatch(
            ListWithToolbarTitleActions.Success(
                moviesList.toList()
            )
        )
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
            if (sessionID == GUEST_SESSION_ID) {
                dispatch(ListWithToolbarTitleActions.LoginRequired("Please login to access your watchlist movies"))
            } else {
                dispatch(ListWithToolbarTitleActions.Request)
                fetchApi(1)
            }
        } else if (sessionID != GUEST_SESSION_ID && this::watchlistMovies.isInitialized) {
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

    fun onLoginRequiredBtnClicked() {
        signOutUseCase.signOutUser(null)
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