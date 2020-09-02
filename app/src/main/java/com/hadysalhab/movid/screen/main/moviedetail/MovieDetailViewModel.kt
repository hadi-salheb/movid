package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.usecases.favmovies.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val addRemoveFavMovieUseCase: AddRemoveFavMovieUseCase,
    private val dataValidator: DataValidator
) : ViewModel(), FetchMovieDetailUseCase.Listener, AddRemoveFavMovieUseCase.Listener {
    private lateinit var movieDetail: MovieDetail

    init {
        fetchMovieDetailUseCase.registerListener(this)
        addRemoveFavMovieUseCase.registerListener(this)
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
                if(!dataValidator.isMovieDetailValid(movieDetail)){
                    fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                        movieID
                    )
                }
            }
        }

    }

    override fun onFetchMovieDetailSuccess(movieDetail: MovieDetail) {
        this.movieDetail = movieDetail
        _viewState.value = DetailLoaded(movieDetail)
    }

    override fun onFetchMovieDetailFailed(msg: String) {
        _viewState.value = Error(msg)
    }

    override fun onFetchingMovieDetail() {
        _viewState.value = Loading
    }

    fun addMovieToFavorites(movieID: Int) {
        if (addRemoveFavMovieUseCase.isBusy) {
            return
        }
        addRemoveFavMovieUseCase.addRemoveFavUseCase(movieID, true)
    }

    fun removeMovieFromFavorites(movieID: Int) {
        if (addRemoveFavMovieUseCase.isBusy) {
            return
        }
        addRemoveFavMovieUseCase.addRemoveFavUseCase(movieID, false)
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
        addRemoveFavMovieUseCase.unregisterListener(this)
    }

}