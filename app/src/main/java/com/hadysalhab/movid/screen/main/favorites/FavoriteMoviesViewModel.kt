package com.hadysalhab.movid.screen.main.favorites

import androidx.lifecycle.LiveData
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
    private val favoritesScreenStateManager: FavoritesScreenStateManager,
    private val schemaToModelHelper: SchemaToModelHelper
) : ViewModel(), FetchFavoriteMoviesUseCase.Listener {

    private lateinit var favoriteMovies: MoviesResponse
    private var moviesList = setOf<Movie>()
    private var numberOfAddedMovies = 0
    val state: LiveData<FavoritesScreenState>
        get() = favoritesScreenStateManager.stateLiveData
    private var isFirstRender = true

    init {
        EventBus.getDefault().register(this)
        fetchFavoriteMoviesUseCase.registerListener(this)
    }

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
                favoritesScreenStateManager.dispatch(
                    FavoritesScreenActions.FavoritesSuccess(
                        moviesList.toList()
                    )
                )
            }
            is MovieDetailEvents.RemoveMovieFromFav -> {
                moviesList = moviesList.filter { it.id != event.movieDetail.details.id }.toSet()
                favoritesScreenStateManager.dispatch(
                    FavoritesScreenActions.FavoritesSuccess(
                        moviesList.toList()
                    )
                )
            }
        }
    }


    fun onStart() {
        if (isFirstRender) {
            isFirstRender = false
            favoritesScreenStateManager.dispatch(FavoritesScreenActions.FavoritesRequest)
            fetchApi(1)
        } else if (state.value!!.data.isNotEmpty()) {
            val numberOfDisplayedMovies = state.value!!.data.size
            if ((numberOfDisplayedMovies < MAX_NUMBER_OF_DATA_PER_PAGE * this.favoriteMovies.page) && (this.favoriteMovies.page < this.favoriteMovies.total_pages)) {
                favoritesScreenStateManager.dispatch(FavoritesScreenActions.FavoritesRequest)
                fetchApi(this.favoriteMovies.page)
            }
        }
    }

    private fun fetchApi(page: Int) {
        fetchFavoriteMoviesUseCase.fetchFavoriteMoviesUseCase(
            page = page
        )
    }

    //UserInteractions------------------------------------------------------------------------------

    fun loadMore() {
        if (fetchFavoriteMoviesUseCase.isBusy || this.favoriteMovies.page + 1 > this.favoriteMovies.total_pages) {
            return
        }
        favoritesScreenStateManager.dispatch(FavoritesScreenActions.Pagination)
        fetchApi(this.favoriteMovies.page + 1)
    }

    fun onRetry() {
        favoritesScreenStateManager.dispatch(FavoritesScreenActions.FavoritesRequest)
        if (this::favoriteMovies.isInitialized) {
            fetchApi(this.favoriteMovies.page)
        } else {
            fetchApi(1)
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onFetchFavoriteMoviesSuccess(apiMoviesResponse: MoviesResponse) {
        this.favoriteMovies = apiMoviesResponse
        moviesList = moviesList.toMutableSet().apply {
            addAll(apiMoviesResponse.movies ?: emptySet())
        }
        favoritesScreenStateManager.dispatch(FavoritesScreenActions.FavoritesSuccess(moviesList.toList()))
    }

    override fun onFetchFavoriteMoviesFailure(msg: String) {
        favoritesScreenStateManager.dispatch(FavoritesScreenActions.FavoritesError(msg))
    }

    override fun onCleared() {
        super.onCleared()
        fetchFavoriteMoviesUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }

}