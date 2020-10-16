package com.hadysalhab.movid.screen.main.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.screen.common.viewmodels.SavedStateViewModel
import javax.inject.Inject

private const val FILTER_SCREEN_STATE_KEY = "com.hadysalhab.movid.screen.main.filter.key"
private const val FILTER_STATE_STORE = "com.hadysalhab.movid.screen.main.filter.store.key"

class FilterViewModel @Inject constructor(
    private val filterScreenStateManager: FilterScreenStateManager,
    private val filterStateStore: DiscoverMoviesFilterStateStore
) : SavedStateViewModel() {
    private lateinit var savedStateHandle: SavedStateHandle
    private val dispatch = filterScreenStateManager::dispatch
    private lateinit var currentStoreState: FilterState
    lateinit var currentScreenState: LiveData<FilterState>

    override fun init(savedStateHandle: SavedStateHandle) {
        this.savedStateHandle = savedStateHandle
        checkCurrentScreenState()
        checkCurrentStoreState()
    }

    fun onSortByChanged(sortBy: String) {
        dispatch(FilterActions.UpdateSortBy(sortBy))
    }

    fun onIncludeAdultChanged(includeAdult: Boolean) {
        dispatch(FilterActions.UpdateIncludeAdult(includeAdult))
    }

    fun onPrimaryReleaseYearGteChanged(primaryReleaseYearGte: String?) {
        dispatch(FilterActions.UpdateReleasedYearFrom(primaryReleaseYearGte))
    }

    fun onPrimaryReleaseYearLteChanged(primaryReleaseYearLte: String?) {
        dispatch(FilterActions.UpdateReleasedYearTo(primaryReleaseYearLte))
    }

    fun onVoteAverageGteChanged(voteAverageGte: Float?) {
        dispatch(FilterActions.UpdateVoteAverageGte(voteAverageGte))
    }

    fun onVoteAverageLteChanged(voteAverageLte: Float?) {
        dispatch(FilterActions.UpdateVoteAverageLte(voteAverageLte))
    }


    //Process Death checks
    private fun checkCurrentScreenState() {
        if (savedStateHandle.contains(FILTER_SCREEN_STATE_KEY)) {
            this.currentScreenState = filterScreenStateManager.setInitialStateAndReturn(
                savedStateHandle.get<FilterState>(
                    FILTER_SCREEN_STATE_KEY
                )!!
            )
        } else {
            this.currentScreenState =
                filterScreenStateManager.setInitialStateAndReturn(filterStateStore.currentFilterState)
        }
    }

    //Process Death checks
    private fun checkCurrentStoreState() {
        if (savedStateHandle.contains(FILTER_STATE_STORE)) {
            val savedStoreState = savedStateHandle.get<FilterState>(FILTER_STATE_STORE)
            filterStateStore.updateStoreState(savedStoreState!!)
            this.currentStoreState = savedStoreState
        } else {
            this.currentStoreState = filterStateStore.currentFilterState
        }
    }

    fun onSavedInstanceState() {
        savedStateHandle.set(FILTER_SCREEN_STATE_KEY, this.currentScreenState.value)
        savedStateHandle.set(FILTER_STATE_STORE, this.currentStoreState)
    }

    fun onFilterSubmit() {
        filterStateStore.updateStoreState(this.currentScreenState.value!!)
    }
}




