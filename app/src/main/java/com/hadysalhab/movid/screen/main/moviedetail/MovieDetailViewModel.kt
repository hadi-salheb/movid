package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.usecases.favorite.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.account.usecases.watchlist.AddRemoveWatchlistMovieUseCase
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.screen.common.events.FavoritesEvent
import com.hadysalhab.movid.screen.common.events.WatchlistEvent
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val addRemoveFavMovieUseCase: AddRemoveFavMovieUseCase,
    private val addRemoveWatchlistMovieUseCase: AddRemoveWatchlistMovieUseCase
) : ViewModel(), FetchMovieDetailUseCase.Listener, AddRemoveFavMovieUseCase.Listener,
    AddRemoveWatchlistMovieUseCase.Listener {
    private lateinit var movieDetail: MovieDetail

    init {
        fetchMovieDetailUseCase.registerListener(this)
        addRemoveFavMovieUseCase.registerListener(this)
        addRemoveWatchlistMovieUseCase.registerListener(this)
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
                fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                    movieID
                )
            }
        }
    }

    fun addMovieToWatchlist(movieID: Int) {
        if (addRemoveWatchlistMovieUseCase.isBusy) {
            return
        }
        _viewState.value = AccountStateLoading
        addRemoveWatchlistMovieUseCase.addRemoveWatchlistUseCase(movieID, true)
    }

    fun addMovieToFavorites(movieID: Int) {
        if (addRemoveFavMovieUseCase.isBusy) {
            return
        }
        _viewState.value = AccountStateLoading
        addRemoveFavMovieUseCase.addRemoveFavUseCase(movieID, true)
    }

    fun removeMovieFromFavorites(movieID: Int) {
        if (addRemoveFavMovieUseCase.isBusy) {
            return
        }
        _viewState.value = AccountStateLoading
        addRemoveFavMovieUseCase.addRemoveFavUseCase(movieID, false)
    }

    fun removeMovieFromWatchlist(movieID: Int) {
        if (addRemoveWatchlistMovieUseCase.isBusy) {
            return
        }
        _viewState.value = AccountStateLoading
        addRemoveWatchlistMovieUseCase.addRemoveWatchlistUseCase(movieID, false)
    }

    //UseCaseResults--------------------------------------------------------------------------------
    override fun onAddRemoveFavoritesSuccess(movieDetail: MovieDetail) {
        this.movieDetail = movieDetail
        if (this.movieDetail.accountStates.favorite) {
            EventBus.getDefault().post(FavoritesEvent.AddMovieToFav(this.movieDetail))
        } else {
            EventBus.getDefault().post(FavoritesEvent.RemoveMovieFromFav(this.movieDetail))
        }
        _viewState.value = DetailLoaded(this.movieDetail)
    }

    override fun onAddRemoveFavoritesFailure(err: String) {
        _viewState.value = Error(err)
    }

    override fun onAddRemoveWatchlistSuccess(movieDetail: MovieDetail) {
        this.movieDetail = movieDetail
        if (this.movieDetail.accountStates.watchlist) {
            EventBus.getDefault().post(WatchlistEvent.AddToWatchlist(this.movieDetail))
        } else {
            EventBus.getDefault().post(WatchlistEvent.RemoveFromWatchlist(this.movieDetail))
        }
        _viewState.value = DetailLoaded(this.movieDetail)
    }

    override fun onAddRemoveWatchlistFailure(err: String) {
        _viewState.value = Error(err)
    }

    override fun onFetchMovieDetailSuccess(movieDetail: MovieDetail) {
        this.movieDetail = movieDetail
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
        addRemoveWatchlistMovieUseCase.unregisterListener(this)
    }

}