package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.MovieDetail

class MovieDetailScreenStateManager {
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _data = MutableLiveData<MovieDetail>(null)
    val data: LiveData<MovieDetail>
        get() = _data

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    fun showUserLoadingScreen() {
        _isLoading.value = true
        _errorMessage.value = null
    }

    fun showUserErrorScreen(errorMessage: String) {
        _isLoading.value = false
        _errorMessage.value = errorMessage
    }

    fun showMovieDetail(movieDetail: MovieDetail?) {
        _errorMessage.value = null
        _isLoading.value = false
        _data.value = movieDetail
    }

    fun showAddRemoveFavoritesFailure() {
        _isLoading.value = false
    }

    fun showAddRemoveWatchListFailure() {
        _isLoading.value = false
    }

    fun showUserRefresh() {
        _isRefreshing.value = true
    }

    fun showUserRefreshSuccess(movieDetail: MovieDetail) {
        _isRefreshing.value = false
        _data.value = movieDetail
    }

    fun showUserRefreshError() {
        _isRefreshing.value = false
    }

    fun isRefreshing(): Boolean = isRefreshing.value!!
}