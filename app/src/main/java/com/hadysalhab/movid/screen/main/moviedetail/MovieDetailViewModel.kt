package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.usecases.favmovies.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val addRemoveFavMovieUseCase: AddRemoveFavMovieUseCase
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
                _viewState.value = Loading
                fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                    movieID
                )
            }
            Loading, is Error -> {
                return
            }
            is DetailLoaded -> {
                _viewState.value = Loading
                fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                    movieID
                )
            }
        }
    }

    fun addMovieToFavorites(movieID: Int) {
        if (addRemoveFavMovieUseCase.isBusy) {
            return
        }
        _viewState.value = FavLoading
        addRemoveFavMovieUseCase.addRemoveFavUseCase(movieID, true)
    }

    fun removeMovieFromFavorites(movieID: Int) {
        if (addRemoveFavMovieUseCase.isBusy) {
            return
        }
        _viewState.value = FavLoading
        addRemoveFavMovieUseCase.addRemoveFavUseCase(movieID, false)
    }

    //UseCaseResults--------------------------------------------------------------------------------
    override fun onAddRemoveFavoritesSuccess(movieDetail: MovieDetail) {
        this.movieDetail = movieDetail
        _viewState.value = DetailLoaded(this.movieDetail)
    }

    override fun onAddRemoveFavoritesFailure(err: String) {
        _viewState.value = Error(err)
    }

    override fun onFetchMovieDetailSuccess(apiMovieDetail: MovieDetail) {
        this.movieDetail = apiMovieDetail
        _viewState.value = DetailLoaded(this.movieDetail)
    }

    override fun onFetchMovieDetailFailed(msg: String) {
        _viewState.value = Error(msg)
    }
//----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchMovieDetailUseCase.unregisterListener(this)
        addRemoveFavMovieUseCase.unregisterListener(this)
    }

}