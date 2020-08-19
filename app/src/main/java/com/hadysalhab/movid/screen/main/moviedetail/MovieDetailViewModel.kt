package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.user.UserStateManager
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val userStateManager: UserStateManager
) : ViewModel(), FetchMovieDetailUseCase.Listener {

    private val _viewState = MutableLiveData<MovieDetailViewState>()
    val viewState: LiveData<MovieDetailViewState>
        get() = _viewState

    init {
        _viewState.value = Loading

    }

    fun getMovieDetail(movieID: Int) {
        fetchMovieDetailUseCase.registerListener(this)
        fetchMovieDetailUseCase.fetchMovieDetailAndNotify(movieID, userStateManager.sessionId)
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
    }


}