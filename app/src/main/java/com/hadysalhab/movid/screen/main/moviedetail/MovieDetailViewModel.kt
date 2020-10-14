package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.usecases.favorite.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.account.usecases.watchlist.AddRemoveWatchlistMovieUseCase
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.screen.common.events.MovieDetailEvents
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

    private val emitter: EventEmitter<MovieDetailScreenEvents> = EventEmitter()
    val screenEvents: EventSource<MovieDetailScreenEvents> get() = emitter

    val state: LiveData<MovieDetailScreenState> = movieDetailScreenStateManager.stateLiveData


    init {
        fetchMovieDetailUseCase.registerListener(this)
        addRemoveFavMovieUseCase.registerListener(this)
        addRemoveWatchlistMovieUseCase.registerListener(this)
        EventBus.getDefault().register(this)
    }

    fun onStart(movieID: Int) {
        val movieDetailStored = moviesStateManager.getMovieDetailById(movieID)
        if (isFirstRender) {
            this.movieID = movieID
            isFirstRender = false
            if (dataValidator.isMovieDetailValid(movieDetailStored)) {
                movieDetailScreenStateManager.dispatch(
                    MovieDetailActions.Success(
                        movieDetailStored!!
                    )
                )
            } else {
                movieDetailScreenStateManager.dispatch(MovieDetailActions.Request)
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
        movieDetailScreenStateManager.dispatch(MovieDetailActions.Refresh)
        fetchApiForMovieDetail()
    }

    fun retry() {
        // double retry clicked
        if (fetchMovieDetailUseCase.isBusy) {
            return
        }
        movieDetailScreenStateManager.dispatch(MovieDetailActions.Request)
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
            movieDetailScreenStateManager.dispatch(MovieDetailActions.Request)
            addRemoveWatchlistMovieUseCase.addRemoveWatchlistUseCase(
                state.value!!.data!!.details.id,
                !state.value!!.data!!.accountStates.watchlist
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
            movieDetailScreenStateManager.dispatch(MovieDetailActions.Request)
            addRemoveFavMovieUseCase.addRemoveFavUseCase(
                state.value!!.data!!.details.id,
                !state.value!!.data!!.accountStates.favorite
            )
        }
    }
    //-------------------------------------------------------------------------------------------

    private fun isAddRemoveFavMovieUseCaseBusy() = addRemoveFavMovieUseCase.isBusy
    private fun isAddRemoveWatchlistMovieUseCaseBusy() = addRemoveWatchlistMovieUseCase.isBusy
    private fun areAddRemoveWatchListOrFavoriteUseCaseBusy() =
        isAddRemoveFavMovieUseCaseBusy() || isAddRemoveWatchlistMovieUseCaseBusy()

    //UseCaseResults--------------------------------------------------------------------------------
    //Failure
    override fun onAddRemoveFavoritesFailure(err: String) {
        emitter.emit(ShowUserToastMessage(err))
        movieDetailScreenStateManager.dispatch(MovieDetailActions.WatchlistFavoriteError)
    }

    override fun onAddRemoveWatchlistFailure(err: String) {
        emitter.emit(ShowUserToastMessage(err))
        movieDetailScreenStateManager.dispatch(MovieDetailActions.WatchlistFavoriteError)
    }

    override fun onFetchMovieDetailFailed(msg: String) {
        if (state.value!!.isRefreshing) {
            movieDetailScreenStateManager.dispatch(MovieDetailActions.RefreshError)
            emitter.emit(ShowUserToastMessage(msg))
        } else {
            movieDetailScreenStateManager.dispatch(MovieDetailActions.Error(msg))
        }
    }


//----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchMovieDetailUseCase.unregisterListener(this)
        addRemoveFavMovieUseCase.unregisterListener(this)
        addRemoveWatchlistMovieUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMovieDetailEvent(event: MovieDetailEvents) {
        if (event.movieDetail.details.id == this.movieID) {
            if (state.value!!.isRefreshing) {
                movieDetailScreenStateManager.dispatch(MovieDetailActions.Success(event.movieDetail))
                emitter.emit(ShowUserToastMessage("Movie Updated"))
            } else {
                movieDetailScreenStateManager.dispatch(MovieDetailActions.Success(event.movieDetail))
            }
        }
    }
}
