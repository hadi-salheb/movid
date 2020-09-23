package com.hadysalhab.movid.screen.main.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.constants.MAX_NUMBER_OF_DATA_PER_PAGE
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.watchlist.FetchWatchlistMoviesUseCase
import com.hadysalhab.movid.screen.common.events.WatchlistEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class WatchlistMoviesViewModel @Inject constructor(
    private val fetchWatchlistMoviesUseCase: FetchWatchlistMoviesUseCase,
    private val schemaToModelHelper: SchemaToModelHelper
) : ViewModel(), FetchWatchlistMoviesUseCase.Listener {
    private val _viewState = MutableLiveData<WishlistMoviesViewState>()
    private lateinit var watchlistMovies: MoviesResponse
    private var numberOfAddedMovies = 0
    private var moviesList = setOf<Movie>()
    val viewState: LiveData<WishlistMoviesViewState>
        get() = _viewState

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onWishlistEvent(event: WatchlistEvent) {
        if (!this::watchlistMovies.isInitialized) {
            return
        }
        moviesList = when (event) {
            is WatchlistEvent.AddToWatchlist -> {
                numberOfAddedMovies++
                val oldMovieSet = this.moviesList
                val newMovieSet = mutableSetOf(
                    schemaToModelHelper.getMovieFromMovieDetail(
                        event.movieDetail
                    )
                )
                newMovieSet.addAll(oldMovieSet)
                if (numberOfAddedMovies == MAX_NUMBER_OF_DATA_PER_PAGE) {
                    watchlistMovies = watchlistMovies.copy(
                        page = (newMovieSet.size + MAX_NUMBER_OF_DATA_PER_PAGE - 1) / MAX_NUMBER_OF_DATA_PER_PAGE,
                        total_pages = watchlistMovies.total_pages + 1
                    )
                    numberOfAddedMovies = 0
                }
                newMovieSet
            }
            is WatchlistEvent.RemoveFromWatchlist -> {
                moviesList.filter { it.id != event.movieDetail.details.id }.toSet()
            }
        }
        _viewState.value = WatchlistMoviesLoaded(moviesList.toList())
    }


    fun onStart() {
        when (_viewState.value) {
            null -> {
                EventBus.getDefault().register(this)
                fetchWatchlistMoviesUseCase.registerListener(this)
                _viewState.value = Loading
                fetchWatchlistMoviesUseCase.fetchWatchlistUseCase(
                    page = 1
                )
            }
            is Loading, is Error -> {

            }
            is WatchlistMoviesLoaded -> {
                val numberOfDisplayedMovies = moviesList.size
                if ((numberOfDisplayedMovies < MAX_NUMBER_OF_DATA_PER_PAGE * this.watchlistMovies.page) && (this.watchlistMovies.page < this.watchlistMovies.total_pages)) {
                    _viewState.value = Loading
                    fetchWatchlistMoviesUseCase.fetchWatchlistUseCase(
                        this.watchlistMovies.page
                    )
                }
            }
        }
    }

    fun loadMore() {
        if (fetchWatchlistMoviesUseCase.isBusy || this.watchlistMovies.page + 1 > this.watchlistMovies.total_pages) {
            return
        }
        _viewState.value = PaginationLoading
        fetchWatchlistMoviesUseCase.fetchWatchlistUseCase(
            page = this.watchlistMovies.page + 1
        )
    }

    //----------------------------------------------------------------------------------------------
    override fun onFetchWatchlistMoviesSuccess(apiMoviesResponse: MoviesResponse) {
        this.watchlistMovies = apiMoviesResponse
        moviesList = moviesList.toMutableSet().apply {
            addAll(apiMoviesResponse.movies ?: emptySet())
        }
        _viewState.value = WatchlistMoviesLoaded(moviesList.toList())
    }

    override fun onFetchWatchlistMoviesFailure(msg: String) {
        _viewState.value = Error(msg)
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchWatchlistMoviesUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }


}