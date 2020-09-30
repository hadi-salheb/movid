package com.hadysalhab.movid.screen.main.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.constants.MAX_NUMBER_OF_DATA_PER_PAGE
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.favorites.FetchFavoriteMoviesUseCase
import com.hadysalhab.movid.screen.common.events.MovieDetailEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class FavoriteMoviesViewModel @Inject constructor(
    private val fetchFavoriteMoviesUseCase: FetchFavoriteMoviesUseCase,
    private val schemaToModelHelper: SchemaToModelHelper
) : ViewModel(), FetchFavoriteMoviesUseCase.Listener {
    private val _viewState = MutableLiveData<FavoriteMoviesViewState>()
    private lateinit var favoriteMovies: MoviesResponse
    private var moviesList = setOf<Movie>()
    private var numberOfAddedMovies = 0
    val viewState: LiveData<FavoriteMoviesViewState>
        get() = _viewState

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onFavoriteEvent(event: MovieDetailEvents) {
        if (!this::favoriteMovies.isInitialized) {
            return
        }
        when (event) {
            is MovieDetailEvents.AddMovieToFav -> {
                numberOfAddedMovies++
                val oldMovieSet = this.moviesList
                val newMovieSet = mutableSetOf(
                    schemaToModelHelper.getMovieFromMovieDetail(
                        event.movieDetail
                    )
                )
                newMovieSet.addAll(oldMovieSet)
                if (numberOfAddedMovies == MAX_NUMBER_OF_DATA_PER_PAGE) {
                    favoriteMovies = favoriteMovies.copy(
                        page = (newMovieSet.size + MAX_NUMBER_OF_DATA_PER_PAGE - 1) / MAX_NUMBER_OF_DATA_PER_PAGE,
                        total_pages = favoriteMovies.total_pages + 1
                    )
                    numberOfAddedMovies = 0
                }
                moviesList = newMovieSet
                _viewState.value = FavoriteMoviesLoaded(moviesList.toList())
            }
            is MovieDetailEvents.RemoveMovieFromFav -> {
                moviesList = moviesList.filter { it.id != event.movieDetail.details.id }.toSet()
                _viewState.value = FavoriteMoviesLoaded(moviesList.toList())
            }
        }
    }


    fun onStart() {
        when (_viewState.value) {
            null -> {
                EventBus.getDefault().register(this)
                fetchFavoriteMoviesUseCase.registerListener(this)
                _viewState.value = Loading
                fetchFavoriteMoviesUseCase.fetchFavoriteMoviesUseCase(
                    page = 1
                )
            }
            is Loading, is Error -> {

            }
            is FavoriteMoviesLoaded -> {
                val numberOfDisplayedMovies = moviesList.size
                if ((numberOfDisplayedMovies < MAX_NUMBER_OF_DATA_PER_PAGE * this.favoriteMovies.page) && (this.favoriteMovies.page < this.favoriteMovies.total_pages)) {
                    _viewState.value = Loading
                    fetchFavoriteMoviesUseCase.fetchFavoriteMoviesUseCase(
                        this.favoriteMovies.page
                    )
                }
            }
        }
    }

    fun loadMore() {
        if (fetchFavoriteMoviesUseCase.isBusy || this.favoriteMovies.page + 1 > this.favoriteMovies.total_pages) {
            return
        }
        _viewState.value = PaginationLoading
        fetchFavoriteMoviesUseCase.fetchFavoriteMoviesUseCase(
            page = this.favoriteMovies.page + 1
        )
    }

    override fun onFetchFavoriteMoviesSuccess(apiMoviesResponse: MoviesResponse) {
        this.favoriteMovies = apiMoviesResponse
        moviesList = moviesList.toMutableSet().apply {
            addAll(apiMoviesResponse.movies ?: emptySet())
        }
        _viewState.value = FavoriteMoviesLoaded(moviesList.toList())
    }

    override fun onFetchFavoriteMoviesFailure(msg: String) {
        _viewState.value = Error(msg)
    }

    override fun onCleared() {
        super.onCleared()
        fetchFavoriteMoviesUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }

}