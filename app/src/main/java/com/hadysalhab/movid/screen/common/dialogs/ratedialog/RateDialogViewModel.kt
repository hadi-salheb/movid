package com.hadysalhab.movid.screen.common.dialogs.ratedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.usecases.rate.RateMovieUseCase
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.screen.common.events.MovieDetailEvents
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

sealed class RateDialogEvent {
    object Dismiss : RateDialogEvent()
}

class RateDialogViewModel
@Inject constructor(
    private val rateMovieUseCase: RateMovieUseCase,
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase
) : ViewModel(), RateMovieUseCase.Listener, FetchMovieDetailUseCase.Listener {
    init {
        rateMovieUseCase.registerListener(this)
        fetchMovieDetailUseCase.registerListener(this)
        EventBus.getDefault().register(this)
    }

    private var isFirstRender = true
    private val initialState = RateViewState(isLoading = false, rate = null, title = "")
    private val _rateDialogViewState = MutableLiveData<RateViewState>(initialState)
    val rateDialogViewState: LiveData<RateViewState>
        get() = _rateDialogViewState
    private var movieId: Int = 0

    private val emitter: EventEmitter<RateDialogEvent> = EventEmitter()
    val screenEvents: EventSource<RateDialogEvent> get() = emitter

    fun onStart(movieName: String, currentRating: Double?, movieId: Int) {
        if (isFirstRender) {
            isFirstRender = false
            this.movieId = movieId
            _rateDialogViewState.value =
                _rateDialogViewState.value!!.copy(
                    title = "Rating: $movieName",
                    rate = currentRating
                )
        }
    }

    fun onRateChange(rate: Double) {
        _rateDialogViewState.value = _rateDialogViewState.value!!.copy(rate = rate)
    }

    fun onPositiveBtnClicked() {
        _rateDialogViewState.value = _rateDialogViewState.value!!.copy(isLoading = true)
        rateMovieUseCase.rateMovieUseCase(this.movieId, _rateDialogViewState.value!!.rate!!)
    }

    override fun onRatingMovieFailure(err: String) {
        _rateDialogViewState.value = _rateDialogViewState.value!!.copy(isLoading = false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMovieDetailEvent(event: MovieDetailEvents) {
        when (event) {
            is MovieDetailEvents.RatingUpdate -> {
                if (event.movieDetail.details.id == this.movieId) {
                    fetchMovieDetailUseCase.fetchMovieDetailAndNotify(this.movieId)
                }
            }
            is MovieDetailEvents.MovieDetailFetched -> {
                if (event.movieDetail.details.id == this.movieId && !rateMovieUseCase.isBusy) {
                    emitter.emit(RateDialogEvent.Dismiss)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        rateMovieUseCase.unregisterListener(this)
        fetchMovieDetailUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }

    override fun onFetchMovieDetailFailed(msg: String) {
        _rateDialogViewState.value = _rateDialogViewState.value!!.copy(isLoading = false)
    }

}