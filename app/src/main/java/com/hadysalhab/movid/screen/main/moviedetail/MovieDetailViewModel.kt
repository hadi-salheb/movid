package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.usecases.favmovies.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val addRemoveFavMovieUseCase: AddRemoveFavMovieUseCase,
    private val dataValidator: DataValidator,
    private val moviesStateManager: MoviesStateManager,
    private val timeProvider: TimeProvider
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
                val movieDetailStore = moviesStateManager.getMovieDetailById(movieID)
                if (movieDetailStore != null && dataValidator.isMovieDetailValid(movieDetailStore)) {
                    movieDetail = movieDetailStore
                    _viewState.value = DetailLoaded(movieDetailStore)
                } else {
                    fetchApiForUpdatedDetail(movieID)
                }
            }
            Loading, is Error -> {
                return
            }
            is DetailLoaded -> {
                val movieDetailStore = moviesStateManager.getMovieDetailById(movieID)
                    ?: throw RuntimeException("Movie Store cannot be null, if detail is loaded")
                if (!dataValidator.isMovieDetailValid(movieDetailStore)) {
                    fetchApiForUpdatedDetail(movieID)
                } else {
                    // because movie detail can be updated from different fragment!
                    if (this.movieDetail != movieDetailStore) {
                        movieDetail = movieDetailStore
                        _viewState.value = DetailLoaded(movieDetailStore)
                    }
                }
            }
        }
    }

    private fun fetchApiForUpdatedDetail(movieID: Int) {
        _viewState.value = Loading
        fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
            movieID
        )
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

    override fun onAddRemoveFavoritesSuccess() {
        val timestamp = this.movieDetail.timeStamp
        this.movieDetail = this.movieDetail.copy(
            accountStates = this.movieDetail.accountStates.copy(
                favorite = !this.movieDetail.accountStates.favorite
            )
        )
        this.movieDetail.timeStamp = timestamp
        moviesStateManager.upsertMovieDetailToList(this.movieDetail)
        _viewState.value = DetailLoaded(this.movieDetail)
    }

    override fun onAddRemoveFavoritesFailure(err: String) {
        _viewState.value = Error(err)
    }

    override fun onFetchMovieDetailSuccess(movieDetail: MovieDetail) {
        this.movieDetail = movieDetail
        this.movieDetail.timeStamp = timeProvider.currentTimestamp
        moviesStateManager.upsertMovieDetailToList(this.movieDetail)
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