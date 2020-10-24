package com.hadysalhab.movid.screen.main.castlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.Credits
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.credits.FetchCreditsUseCase
import com.hadysalhab.movid.screen.common.people.People
import com.hadysalhab.movid.screen.common.people.PeopleType
import java.util.*
import javax.inject.Inject

class PeopleListViewModel @Inject
constructor(
    private val fetchCreditsUseCase: FetchCreditsUseCase,
    private val peopleListScreenStateManager: PeopleListScreenStateManager,
    private val moviesStateManager: MoviesStateManager,
    private val dataValidator: DataValidator
) : ViewModel(), FetchCreditsUseCase.Listener {
    private var isFirstRender = true
    private val dispatch = peopleListScreenStateManager::dispatch
    private lateinit var movieName: String
    private lateinit var peopleType: PeopleType
    private var movieId: Int = 0

    init {
        fetchCreditsUseCase.registerListener(this)
    }

    val state: LiveData<PeopleListViewState> =
        peopleListScreenStateManager.setInitialStateAndReturn(
            PeopleListViewState(
                title = "",
                emptyResultsIconDrawable = R.drawable.ic_sad,
                emptyResultsMessage = "No Results Found"
            )
        )

    fun onStart(movieID: Int, movieName: String, peopleType: PeopleType) {
        if (isFirstRender) {
            isFirstRender = false
            this.movieName = movieName
            this.movieId = movieID
            this.peopleType = peopleType
            dispatch(
                PeopleListActions.SetTitle("$movieName (${peopleType.name})".toUpperCase(Locale.ROOT))
            )
            val storedMovie = moviesStateManager.getMovieDetailById(movieID)
            if (dataValidator.isMovieDetailValid(storedMovie)) {
                dispatchSuccess(storedMovie!!.credits)
            } else {
                fetchApi()
            }
        }
    }

    private fun dispatchSuccess(credits: Credits) {
        when (peopleType) {
            PeopleType.CREW -> dispatch(
                PeopleListActions.Success(
                    credits.crew.map {
                        People(it.id, it.name, it.job, it.profilePath, PeopleType.CREW)
                    }
                )
            )
            PeopleType.CAST -> dispatch(PeopleListActions.Success(
                credits.cast.map {
                    People(
                        it.id,
                        it.name,
                        it.character,
                        it.profilePath,
                        PeopleType.CAST
                    )
                }
            ))
        }
    }

    private fun fetchApi() {
    }

    //User Interactions-----------------------------------------------------------------------------
    fun onRetryClicked() {
        fetchApi()
    }

    //UseCaseResults--------------------------------------------------------------------------------
    override fun fetchCreditsSuccess(credits: Credits) {
        dispatchSuccess(credits)
    }

    override fun fetchCreditsFailure(errorMessage: String) {
        dispatch(PeopleListActions.Error(errorMessage))
    }

    //----------------------------------------------------------------------------------------------
    override fun onCleared() {
        super.onCleared()
        fetchCreditsUseCase.unregisterListener(this)
    }
}