package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.AddToFavoriteUseCase
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) : ViewModel(), FetchMovieDetailUseCase.Listener, AddToFavoriteUseCase.Listener {

    private val _viewState = MutableLiveData<MovieDetailViewState>()
    val viewState: LiveData<MovieDetailViewState>
        get() = _viewState

    fun onStart(movieID: Int) {
        when (_viewState.value) {
            null -> {
                fetchMovieDetailUseCase.registerListener(this)
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

    override fun onCleared() {
        super.onCleared()
        fetchMovieDetailUseCase.unregisterListener(this)
        addToFavoriteUseCase.unregisterListener(this)
    }

    fun addMovieToFavorites(movieID: Int) {
        addToFavoriteUseCase.registerListener(this)
        addToFavoriteUseCase.addToFavoriteUseCase(movieID)
    }

    override fun onAddToFavorites() {
        _viewState.value = FavLoading
    }

    override fun onAddToFavoritesSuccess(movieDetail: MovieDetail) {
        _viewState.value = DetailLoaded(movieDetail)
    }

    override fun onAddToFavoritesFailure(err: String) {
        _viewState.value = Error(err)
    }

}