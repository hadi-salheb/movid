package com.hadysalhab.movid.screen.main.castlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.Credits
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.credits.FetchCreditsUseCase
import java.util.*
import javax.inject.Inject

class CastListViewModel @Inject
constructor(
    private val fetchCreditsUseCase: FetchCreditsUseCase,
    private val castListScreenStateManager: CastListScreenStateManager,
    private val moviesStateManager: MoviesStateManager,
    private val dataValidator: DataValidator
) : ViewModel(), FetchCreditsUseCase.Listener {
    private var isFirstRender = true
    private val dispatch = castListScreenStateManager::dispatch
    private lateinit var movieName: String
    private var movieId: Int = 0

    init {
        fetchCreditsUseCase.registerListener(this)
    }

    val state: LiveData<CastListViewState> =
        castListScreenStateManager.setInitialStateAndReturn(
            CastListViewState(
                title = "",
                emptyResultsIconDrawable = R.drawable.ic_watchlist,
                emptyResultsMessage = "No Movies Added To Watchlist"
            )
        )

    fun onStart(movieID: Int, movieName: String) {
        if (isFirstRender) {
            isFirstRender = false
            this.movieName = movieName
            this.movieId = movieID
            dispatch(
                CastListActions.SetTitle("$movieName (CAST)".toUpperCase(Locale.ROOT))
            )
            val storedMovie = moviesStateManager.getMovieDetailById(movieID)
            if (dataValidator.isMovieDetailValid(storedMovie)) {
                dispatch(
                    CastListActions.Success(
                        storedMovie!!.credits.cast
                    )
                )
            } else {
                fetchApi()
            }
        }
    }

    private fun fetchApi() {
        dispatch(CastListActions.Request)
        fetchCreditsUseCase.fetchCreditsUseCase(this.movieId)
    }

    //User Interactions-----------------------------------------------------------------------------
    fun onRetryClicked() {
        fetchApi()
    }

    //UseCaseResults--------------------------------------------------------------------------------
    override fun fetchCreditsSuccess(credits: Credits) {
        dispatch(CastListActions.Success(credits.cast))
    }

    override fun fetchCreditsFailure(errorMessage: String) {
        dispatch(CastListActions.Error(errorMessage))
    }

    //----------------------------------------------------------------------------------------------
    override fun onCleared() {
        super.onCleared()
        fetchCreditsUseCase.unregisterListener(this)
    }
}