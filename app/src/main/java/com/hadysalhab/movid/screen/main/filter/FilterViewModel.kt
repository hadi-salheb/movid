package com.hadysalhab.movid.screen.main.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.movies.FilterStoreState
import com.hadysalhab.movid.screen.common.viewmodels.SavedStateViewModel
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import java.util.*
import javax.inject.Inject

private const val FILTER_SCREEN_STATE_KEY = "com.hadysalhab.movid.screen.main.filter.key"
private const val FILTER_STATE_STORE = "com.hadysalhab.movid.screen.main.filter.store.key"

class FilterViewModel @Inject constructor(
    private val filterScreenStateManager: FilterScreenStateManager,
    private val filterStateStore: DiscoverMoviesFilterStateStore,
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient
) : SavedStateViewModel() {
    private lateinit var savedStateHandle: SavedStateHandle
    private val dispatch = filterScreenStateManager::dispatch
    private lateinit var currentStoreState: FilterStoreState
    lateinit var currentScreenViewState: LiveData<FilterViewState>

    private val emitter: EventEmitter<FilterScreenEvents> = EventEmitter()
    val screenEvents: EventSource<FilterScreenEvents> get() = emitter

    override fun init(savedStateHandle: SavedStateHandle) {
        this.savedStateHandle = savedStateHandle
        checkCurrentScreenState()
        checkCurrentStoreState()
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

    fun onVoteCountLteChanged(voteCountLte: Int?) {
        dispatch(FilterActions.UpdateVoteCountLte(voteCountLte))
    }

    fun onVoteCountGteChanged(voteCountGte: Int?) {
        dispatch(FilterActions.UpdateVoteCountGte(voteCountGte))
    }

    fun onRuntimeGteChanged(withRuntimeGte: Int?) {
        dispatch(FilterActions.UpdateRuntimeGte(withRuntimeGte))
    }

    fun onRuntimeLteChanged(withRuntimeLte: Int?) {
        dispatch(FilterActions.UpdateRuntimeLte(withRuntimeLte))
    }

    fun onResetClick() {
        dispatch(FilterActions.ResetState)
    }


    //Process Death checks
    private fun checkCurrentScreenState() {
        if (savedStateHandle.contains(FILTER_SCREEN_STATE_KEY)) {
            this.currentScreenViewState = filterScreenStateManager.setInitialStateAndReturn(
                savedStateHandle.get<FilterViewState>(
                    FILTER_SCREEN_STATE_KEY
                )!!
            )
        } else {
            this.currentScreenViewState =
                filterScreenStateManager.setInitialStateAndReturn(getScreenStateFromStoreState())
        }
    }

    //Process Death checks
    private fun checkCurrentStoreState() {
        if (savedStateHandle.contains(FILTER_STATE_STORE)) {
            val savedStoreState = savedStateHandle.get<FilterStoreState>(FILTER_STATE_STORE)
            filterStateStore.updateStoreState(savedStoreState!!)
            this.currentStoreState = savedStoreState
        } else {
            this.currentStoreState = filterStateStore.currentFilterState
        }
    }

    private fun getScreenStateFromStoreState(): FilterViewState {
        val sortOption = SortOption.values().find { sortOption ->
            sortOption.sortOptionValue.split(" ").joinToString("_")
                .toLowerCase(Locale.ROOT) == filterStateStore.currentFilterState.sortBy.split(".")[0]
        }
        val sortOrder = SortOrder.values().find { sortOrder ->
            sortOrder.sortOrderValue.toLowerCase(Locale.ROOT) == filterStateStore.currentFilterState.sortBy.split(
                "."
            )[1]
        }

        return FilterViewState(
            sortOption!!,
            sortOrder!!,
            filterStateStore.currentFilterState.includeAdult,
            filterStateStore.currentFilterState.primaryReleaseYearGte,
            filterStateStore.currentFilterState.primaryReleaseYearLte,
            filterStateStore.currentFilterState.voteCountGte,
            filterStateStore.currentFilterState.voteCountLte,
            filterStateStore.currentFilterState.voteAverageGte,
            filterStateStore.currentFilterState.voteAverageLte,
            filterStateStore.currentFilterState.withRuntimeGte,
            filterStateStore.currentFilterState.withRuntimeLte
        )
    }

    private fun getStoreStateFromScreenState(): FilterStoreState {
        val screenState = currentScreenViewState.value!!
        val sortBy =
            screenState.sortByOption.sortOptionValue.split(" ").joinToString("_").toLowerCase(
                Locale.ROOT
            ) + "." + screenState.sortByOrder.sortOrderValue.toLowerCase(
                Locale.ROOT
            )

        return FilterStoreState(
            sortBy,
            screenState.includeAdult,
            screenState.primaryReleaseYearGte,
            screenState.primaryReleaseYearLte,
            screenState.voteCountGte,
            screenState.voteCountLte,
            screenState.voteAverageGte,
            screenState.voteAverageLte,
            screenState.withRuntimeGte,
            screenState.withRuntimeLte


        )
    }


    fun onSavedInstanceState() {
        savedStateHandle.set(FILTER_SCREEN_STATE_KEY, this.currentScreenViewState.value)
        savedStateHandle.set(FILTER_STATE_STORE, this.currentStoreState)
    }

    fun onFilterSubmit() {
        val stateValue = currentScreenViewState.value!!
        if (stateValue.voteCountGte != null && stateValue.voteCountLte != null && stateValue.voteCountGte > stateValue.voteCountLte) {
            emitter.emit(FilterScreenEvents.ShowToast("Please check your vote count inputs"))
            firebaseAnalyticsClient.logFilterFailure("VoteCount")
        } else if (stateValue.withRuntimeGte != null && stateValue.withRuntimeLte != null && stateValue.withRuntimeGte > stateValue.withRuntimeLte) {
            emitter.emit(FilterScreenEvents.ShowToast("Please check your runtime inputs"))
            firebaseAnalyticsClient.logFilterFailure("Runtime")
        } else {
            filterStateStore.updateStoreState(getStoreStateFromScreenState())
            firebaseAnalyticsClient.logFilterSuccess()
            emitter.emit(FilterScreenEvents.PopFragment)
        }
    }

    fun onSortByOrderChanged(sort: SortOrder) {
        dispatch(FilterActions.UpdateSortByOrder(sort))
    }

    fun onSortByOptionChanged(sortByOption: SortOption) {
        dispatch(FilterActions.UpdateSortByOption(sortByOption))
    }
}




