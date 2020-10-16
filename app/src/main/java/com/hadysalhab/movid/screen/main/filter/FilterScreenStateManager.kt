package com.hadysalhab.movid.screen.main.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

sealed class FilterActions {
    data class UpdateSortBy(val sortBy: String) : FilterActions()
    data class UpdateIncludeAdult(val includeAdult: Boolean) : FilterActions()
    data class UpdateReleasedYearFrom(val releasedYearFrom: String?) : FilterActions()
    data class UpdateReleasedYearTo(val releasedYearTo: String?) : FilterActions()
    data class UpdateVoteAverageGte(val voteAverageGte: Float?) : FilterActions()
    data class UpdateVoteAverageLte(val voteAverageLte: Float?) : FilterActions()
}

class FilterScreenStateManager {
    private val stateLiveData = MutableLiveData<FilterState>()
    fun setInitialStateAndReturn(initialState: FilterState): LiveData<FilterState> {
        stateLiveData.value = initialState
        return stateLiveData
    }

    private var state: FilterState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(filterActions: FilterActions) {
        state = filterStateReducer(filterActions)
    }

    private fun filterStateReducer(filterActions: FilterActions): FilterState =
        when (filterActions) {
            is FilterActions.UpdateSortBy -> state.copy(sortBy = filterActions.sortBy)
            is FilterActions.UpdateIncludeAdult -> state.copy(includeAdult = filterActions.includeAdult)
            is FilterActions.UpdateReleasedYearFrom -> state.copy(primaryReleaseYearGte = filterActions.releasedYearFrom)
            is FilterActions.UpdateReleasedYearTo -> state.copy(primaryReleaseYearLte = filterActions.releasedYearTo)
            is FilterActions.UpdateVoteAverageGte -> state.copy(voteAverageGte = filterActions.voteAverageGte)
            is FilterActions.UpdateVoteAverageLte -> state.copy(voteAverageLte = filterActions.voteAverageLte)
        }
}