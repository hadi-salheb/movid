package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.usecases.favorite.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.account.usecases.watchlist.AddRemoveWatchlistMovieUseCase
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.screen.common.events.FavoritesEvent
import com.hadysalhab.movid.screen.common.events.WatchlistEvent
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.livedatacombinetuplekt.combineTuple
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val addRemoveFavMovieUseCase: AddRemoveFavMovieUseCase,
    private val addRemoveWatchlistMovieUseCase: AddRemoveWatchlistMovieUseCase,
    private val moviesStateManager: MoviesStateManager,
    private val movieDetailScreenStateManager: MovieDetailScreenStateManager,
    private val dataValidator: DataValidator
) : ViewModel(), FetchMovieDetailUseCase.Listener, AddRemoveFavMovieUseCase.Listener,
    AddRemoveWatchlistMovieUseCase.Listener {
    private var isFirstRender = true
    private var movieID: Int? = null

    private val emitter: EventEmitter<MovieDetailEvents> = EventEmitter()
    val events: EventSource<MovieDetailEvents> get() = emitter

    val data: LiveData<MovieDetail>
        get() = movieDetailScreenStateManager.data

    val isLoading: LiveData<Boolean>
        get() = movieDetailScreenStateManager.isLoading

    val errorMessage: LiveData<String?>
        get() = movieDetailScreenStateManager.errorMessage

    val refresh: LiveData<Boolean>
        get() = movieDetailScreenStateManager.isRefreshing

    val combinedState = combineTuple(data, isLoading, errorMessage, refresh)

    init {
        fetchMovieDetailUseCase.registerListener(this)
        addRemoveFavMovieUseCase.registerListener(this)
        addRemoveWatchlistMovieUseCase.registerListener(this)
    }

    fun onStart(movieID: Int) {
        if (isFirstRender) {
            this.movieID = movieID
            isFirstRender = false
            val movieDetailStored = moviesStateManager.getMovieDetailById(movieID)
            if (dataValidator.isMovieDetailValid(movieDetailStored)) {
                movieDetailScreenStateManager.showMovieDetail(movieDetailStored)
            } else {
                movieDetailScreenStateManager.showUserLoadingScreen()
                fetchApiForMovieDetail()
            }
        }
    }

    private fun fetchApiForMovieDetail() {
        fetchMovieDetailUseCase.fetchMovieDetailAndNotify(this.movieID!!)
    }

    //User Actions----------------------------------------------------------------------------------
    fun onRefresh() {
        if (fetchMovieDetailUseCase.isBusy) {
            return
        }
        movieDetailScreenStateManager.showUserRefresh()
        fetchApiForMovieDetail()
    }

    fun retry() {
        // double retry clicked
        if (fetchMovieDetailUseCase.isBusy) {
            return
        }
        movieDetailScreenStateManager.showUserLoadingScreen()
        fetchApiForMovieDetail()
    }

    fun onAddRemoveWatchlistClicked() {
        // user clicked add/remove watchlist while pulling to refresh
        if (fetchMovieDetailUseCase.isBusy) {
            return
        }
        if (areAddRemoveWatchListOrFavoriteUseCaseBusy()) {
            emitter.emit(ShowUserToastMessage("Please Wait"))
        } else {
            movieDetailScreenStateManager.showUserLoadingScreen()
            addRemoveWatchlistMovieUseCase.addRemoveWatchlistUseCase(
                data.value!!.details.id,
                !data.value!!.accountStates.watchlist
            )
        }
    }

    fun onAddRemoveFavoritesClicked() {
        // user clicked add/remove favorites while pulling to refresh
        if (fetchMovieDetailUseCase.isBusy) {
            return
        }
        if (areAddRemoveWatchListOrFavoriteUseCaseBusy()) {
            emitter.emit(ShowUserToastMessage("Please Wait"))
        } else {
            movieDetailScreenStateManager.showUserLoadingScreen()
            addRemoveFavMovieUseCase.addRemoveFavUseCase(
                data.value!!.details.id,
                !data.value!!.accountStates.favorite
            )
        }
    }
    //-------------------------------------------------------------------------------------------

    private fun isAddRemoveFavMovieUseCaseBusy() = addRemoveFavMovieUseCase.isBusy
    private fun isAddRemoveWatchlistMovieUseCaseBusy() = addRemoveWatchlistMovieUseCase.isBusy
    private fun areAddRemoveWatchListOrFavoriteUseCaseBusy() =
        isAddRemoveFavMovieUseCaseBusy() || isAddRemoveWatchlistMovieUseCaseBusy()

    //UseCaseResults--------------------------------------------------------------------------------
    override fun onAddRemoveFavoritesSuccess(movieDetail: MovieDetail) {
        if (movieDetail.accountStates.favorite) {
            EventBus.getDefault().post(FavoritesEvent.AddMovieToFav(movieDetail))
        } else {
            EventBus.getDefault().post(FavoritesEvent.RemoveMovieFromFav(movieDetail))
        }
        movieDetailScreenStateManager.showMovieDetail(movieDetail)
    }

    override fun onAddRemoveWatchlistSuccess(movieDetail: MovieDetail) {
        if (movieDetail.accountStates.watchlist) {
            EventBus.getDefault().post(WatchlistEvent.AddToWatchlist(movieDetail))
        } else {
            EventBus.getDefault().post(WatchlistEvent.RemoveFromWatchlist(movieDetail))
        }
        movieDetailScreenStateManager.showMovieDetail(movieDetail)
    }

    override fun onFetchMovieDetailSuccess(movieDetail: MovieDetail) {
        if (movieDetailScreenStateManager.isRefreshing()) {
            movieDetailScreenStateManager.showUserRefreshSuccess(movieDetail)
            emitter.emit(ShowUserToastMessage("Movie Updated"))
        } else {
            movieDetailScreenStateManager.showMovieDetail(movieDetail)
        }
    }

    override fun onAddRemoveFavoritesFailure(err: String) {
        emitter.emit(ShowUserToastMessage(err))
        movieDetailScreenStateManager.showAddRemoveFavoritesFailure()
    }

    override fun onFetchMovieDetailFailed(msg: String) {
        if (movieDetailScreenStateManager.isRefreshing()) {
            movieDetailScreenStateManager.showUserRefreshError()
            emitter.emit(ShowUserToastMessage(msg))
        } else {
            movieDetailScreenStateManager.showUserErrorScreen(msg)
        }
    }

    override fun onAddRemoveWatchlistFailure(err: String) {
        emitter.emit(ShowUserToastMessage(err))
        movieDetailScreenStateManager.showAddRemoveWatchListFailure()
    }
//----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchMovieDetailUseCase.unregisterListener(this)
        addRemoveFavMovieUseCase.unregisterListener(this)
        addRemoveWatchlistMovieUseCase.unregisterListener(this)
    }




}