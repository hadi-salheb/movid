package com.hadysalhab.movid.screen.main.moviedetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.GetSessionIdUseCaseSync
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    getSessionIdUseCaseSync: GetSessionIdUseCaseSync
) : ViewModel(), FetchMovieDetailUseCase.Listener {
    private val sessionId: String = getSessionIdUseCaseSync.getSessionIdUseCaseSync()

    private val _viewState = MutableLiveData<MovieDetailViewState>()
    val viewState: LiveData<MovieDetailViewState>
        get() = _viewState

    fun onStart(movieID: Int) {
        Log.d("LoadAccount", "loading data")
        when (_viewState.value) {
            null -> {
                fetchMovieDetailUseCase.registerListener(this)
                fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                    movieID,
                    sessionId
                )
            }
            Loading, is Error -> {
                return
            }
            is DetailLoaded -> {
                //check if movie is still valid
                fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                    movieID,
                    sessionId
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
    }


}