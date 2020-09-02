package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.AddRemoveFavUseCase
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val addRemoveFavUseCase: AddRemoveFavUseCase
) : ViewModel(), FetchMovieDetailUseCase.Listener, AddRemoveFavUseCase.Listener {
    init {
        fetchMovieDetailUseCase.registerListener(this)
        addRemoveFavUseCase.registerListener(this)
    }

    private val _viewState = MutableLiveData<MovieDetailViewState>()
    val viewState: LiveData<MovieDetailViewState>
        get() = _viewState

    fun onStart(movieID: Int) {
        when (_viewState.value) {
            null -> {
                fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                    movieID

                )
            }
            Loading, is Error -> {
                return
            }
            is DetailLoaded -> {
                //check if movie is still valid
                fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                    movieID
                )
            }
        }

    }

    override fun onFetchMovieDetailSuccess(movieDetail: MovieDetail) {
        _viewState.value = DetailLoaded(movieDetail)
    }

    override fun onFetchMovieDetailFailed(msg: String) {
        _viewState.value = Error(msg)
    }

    override fun onFetchingMovieDetail() {
        _viewState.value = Loading
    }

    fun addMovieToFavorites(movieID: Int) {
        if (addRemoveFavUseCase.isBusy) {
            return
        }
        addRemoveFavUseCase.addRemoveFavUseCase(movieID, true)
    }

    fun removeMovieFromFavorites(movieID: Int) {
        if (addRemoveFavUseCase.isBusy) {
            return
        }
        addRemoveFavUseCase.addRemoveFavUseCase(movieID, false)
    }

    override fun onAddRemoveFavorites() {
        _viewState.value = FavLoading
    }

    override fun onAddRemoveFavoritesSuccess(movieDetail: MovieDetail) {
        _viewState.value = DetailLoaded(movieDetail)
    }

    override fun onAddRemoveFavoritesFailure(err: String) {
        _viewState.value = Error(err)
    }

    override fun onCleared() {
        super.onCleared()
        fetchMovieDetailUseCase.unregisterListener(this)
        addRemoveFavUseCase.unregisterListener(this)
    }

}