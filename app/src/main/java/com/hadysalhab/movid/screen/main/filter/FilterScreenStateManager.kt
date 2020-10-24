package com.hadysalhab.movid.screen.main.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

sealed class FilterActions {
    data class UpdateIncludeAdult(val includeAdult: Boolean) : FilterActions()
    data class UpdateReleasedYearFrom(val releasedYearFrom: String?) : FilterActions()
    data class UpdateReleasedYearTo(val releasedYearTo: String?) : FilterActions()
    data class UpdateVoteAverageGte(val voteAverageGte: Float?) : FilterActions()
    data class UpdateVoteAverageLte(val voteAverageLte: Float?) : FilterActions()
    data class UpdateVoteCountGte(val voteCountGte: Int?) : FilterActions()
    data class UpdateVoteCountLte(val voteCountLte: Int?) : FilterActions()
    data class UpdateRuntimeGte(val runtimeGte: Int?) : FilterActions()
    data class UpdateRuntimeLte(val runtimeLte: Int?) : FilterActions()
    data class UpdateSortByOrder(val sortOrder: SortOrder) : FilterActions()
    data class UpdateSortByOption(val sortOption: SortOption) : FilterActions()
    object ResetState : FilterActions()
}

class FilterScreenStateManager {
    private val stateLiveData = MutableLiveData<FilterViewState>()
    fun setInitialStateAndReturn(initialViewState: FilterViewState): LiveData<FilterViewState> {
        stateLiveData.value = initialViewState
        return stateLiveData
    }

    private var viewState: FilterViewState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(filterActions: FilterActions) {
        viewState = filterStateReducer(filterActions)
    }

    private fun filterStateReducer(filterActions: FilterActions): FilterViewState =
        when (filterActions) {
            is FilterActions.UpdateIncludeAdult -> viewState.copy(includeAdult = filterActions.includeAdult)
            is FilterActions.UpdateReleasedYearFrom -> viewState.copy(primaryReleaseYearGte = filterActions.releasedYearFrom)
            is FilterActions.UpdateReleasedYearTo -> viewState.copy(primaryReleaseYearLte = filterActions.releasedYearTo)
            is FilterActions.UpdateVoteAverageGte -> viewState.copy(voteAverageGte = filterActions.voteAverageGte)
            is FilterActions.UpdateVoteAverageLte -> viewState.copy(voteAverageLte = filterActions.voteAverageLte)
            is FilterActions.UpdateVoteCountGte -> viewState.copy(voteCountGte = filterActions.voteCountGte)
            is FilterActions.UpdateVoteCountLte -> viewState.copy(voteCountLte = filterActions.voteCountLte)
            is FilterActions.UpdateRuntimeGte -> viewState.copy(withRuntimeGte = filterActions.runtimeGte)
            is FilterActions.UpdateRuntimeLte -> viewState.copy(withRuntimeLte = filterActions.runtimeLte)
            is FilterActions.UpdateSortByOrder -> viewState.copy(sortByOrder = filterActions.sortOrder)
            is FilterActions.UpdateSortByOption -> viewState.copy(sortByOption = filterActions.sortOption)
            FilterActions.ResetState -> FilterViewState()
        }
}