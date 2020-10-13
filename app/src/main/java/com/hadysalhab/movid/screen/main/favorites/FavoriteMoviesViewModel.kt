package com.hadysalhab.movid.screen.main.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.MAX_NUMBER_OF_DATA_PER_PAGE
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.favorites.FetchFavoriteMoviesUseCase
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

class FavoriteMoviesViewModel @Inject constructor(
    private val fetchFavoriteMoviesUseCase: FetchFavoriteMoviesUseCase,
    private val listWithToolbarTitleStateManager: ListWithToolbarTitleStateManager,
    private val schemaToModelHelper: SchemaToModelHelper
) : ViewModel(), FetchFavoriteMoviesUseCase.Listener {

    private lateinit var favoriteMovies: MoviesResponse
    private var moviesList = setOf<Movie>()
    val state: LiveData<ListWithToolbarTitleState> =
        listWithToolbarTitleStateManager.setInitialStateAndReturn(
            ListWithToolbarTitleState(
                title = "FAVORITES",
                movieListScreenState = MovieListScreenState(
                    emptyResultsIconDrawable = R.drawable.ic_favorite,
                    emptyResultsMessage = "No Movies Added To Favorites"
                )
            )
        )
    private var isFirstRender = true
    private val dispatch = listWithToolbarTitleStateManager::dispatch

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
                favoriteMovies = favoriteMovies.copy(favoriteMovies.totalResults + 1)
                val oldMovieSet = this.moviesList
                val newMovieSet = mutableSetOf(
                    schemaToModelHelper.getMovieFromMovieDetail(
                        event.movieDetail
                    )
                )
                newMovieSet.addAll(oldMovieSet) // newSet = [...newAddedMovies + ... oldMovies]
                updateFavoriteMovies()
                moviesList = newMovieSet
                dispatch(
                    ListWithToolbarTitleActions.Success(
                        moviesList.toList()
                    )
                )
            }
            is MovieDetailEvents.RemoveMovieFromFav -> {
                favoriteMovies = favoriteMovies.copy(page = favoriteMovies.totalResults - 1)
                moviesList = moviesList.filter { it.id != event.movieDetail.details.id }.toSet()
                updateFavoriteMovies()
                dispatch(
                    ListWithToolbarTitleActions.Success(
                        moviesList.toList()
                    )
                )
            }
        }

    }

    private fun updateFavoriteMovies() {
        favoriteMovies = favoriteMovies.copy(
            page = max(floor((moviesList.size * 1.0) / MAX_NUMBER_OF_DATA_PER_PAGE), 1.0).toInt(),
            total_pages = ceil(((favoriteMovies.totalResults)) * 1.0 / MAX_NUMBER_OF_DATA_PER_PAGE).toInt()
        )
    }

    fun onStart() {
        if (isFirstRender) {
            isFirstRender = false
            dispatch(ListWithToolbarTitleActions.Request)
            fetchApi(1)
        } else if (this::favoriteMovies.isInitialized) {
            val numberOfDisplayedMovies = moviesList.size
            if (favoriteMovies.page + 1 <= favoriteMovies.total_pages && (numberOfDisplayedMovies < (MAX_NUMBER_OF_DATA_PER_PAGE * (favoriteMovies.page + 1)))) {
                dispatch(ListWithToolbarTitleActions.Request)
                if (favoriteMovies.page == 1) {
                    fetchApi(1)
                }
                fetchApi(this.favoriteMovies.page + 1)
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
        dispatch(ListWithToolbarTitleActions.Pagination)
        fetchApi(this.favoriteMovies.page + 1)
    }

    fun onRetry() {
        dispatch(ListWithToolbarTitleActions.Request)
        if (this::favoriteMovies.isInitialized) {
            fetchApi(this.favoriteMovies.page)
        } else {
            fetchApi(1)
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onFetchFavoriteMoviesSuccess(movies: MoviesResponse) {
        this.favoriteMovies = movies
        moviesList = moviesList.toMutableSet().apply {
            addAll(movies.movies ?: emptySet())
        }
        dispatch(ListWithToolbarTitleActions.Success(moviesList.toList()))
    }

    override fun onFetchFavoriteMoviesFailure(msg: String) {
        if (state.value!!.movieListScreenState.isPaginationLoading) {
            dispatch(ListWithToolbarTitleActions.PaginationError)
        } else {
            dispatch(ListWithToolbarTitleActions.Error(msg))
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchFavoriteMoviesUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }

}