package com.hadysalhab.movid.screen.main.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.discover.DiscoverMoviesUseCase
import com.hadysalhab.movid.screen.common.events.DiscoverEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
    private val schemaToModelHelper: SchemaToModelHelper,
    private val discoverMoviesUseCase: DiscoverMoviesUseCase
) : ViewModel(), DiscoverMoviesUseCase.Listener {
    private val _viewState = MutableLiveData<DiscoverMoviesViewState>()
    private lateinit var discoveredMovies: MoviesResponse
    private var moviesList = listOf<Movie>()
    private lateinit var genreIds: String
    val viewState: LiveData<DiscoverMoviesViewState>
        get() = _viewState
    private var filtersModified = false

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onFilterChangeEvent(event: DiscoverEvents) {

    }


    fun onStart(genreIds: String) {
        if (!this::genreIds.isInitialized) {
            this.genreIds = genreIds
        }
        when (_viewState.value) {
            null -> {
                discoverMoviesUseCase.registerListener(this)
                _viewState.value = Loading
                discoverMoviesUseCase.discoverMoviesUseCase(
                    page = 1,
                    genreIds = genreIds
                )
            }
            is Loading, is Error -> {

            }
            is DiscoverMoviesLoaded -> {

            }
        }
    }

    fun loadMore() {
        if (discoverMoviesUseCase.isBusy || this.discoveredMovies.page + 1 > this.discoveredMovies.total_pages) {
            return
        }
        _viewState.value = PaginationLoading
        discoverMoviesUseCase.discoverMoviesUseCase(
            page = this.discoveredMovies.page + 1,
            genreIds = this.genreIds
        )
    }

    override fun onFetchDiscoverMoviesSuccess(movies: MoviesResponse) {
        this.discoveredMovies = movies
        moviesList = moviesList.toMutableList().apply {
            addAll(movies.movies ?: emptyList())
        }
        _viewState.value = DiscoverMoviesLoaded(moviesList.toList())
    }

    override fun onFetchDiscoverMoviesFailure(msg: String) {
        _viewState.value = Error(msg)
    }

    override fun onCleared() {
        super.onCleared()
        discoverMoviesUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }


}