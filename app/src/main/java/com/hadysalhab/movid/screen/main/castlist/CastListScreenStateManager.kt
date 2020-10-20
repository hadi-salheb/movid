package com.hadysalhab.movid.screen.main.castlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.Cast

sealed class CastListActions {
    object Request : CastListActions()
    data class Error(val errorMessage: String?) : CastListActions()
    data class Success(val data: List<Cast>) : CastListActions()
    data class SetTitle(val title: String) : CastListActions()
}

class CastListScreenStateManager {
    private val stateLiveData = MutableLiveData<CastListViewState>()

    fun setInitialStateAndReturn(initialState: CastListViewState): LiveData<CastListViewState> {
        stateLiveData.value = initialState
        return stateLiveData
    }

    private var state: CastListViewState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(castListActions: CastListActions) {
        state = castListReducer(castListActions)
    }

    private fun castListReducer(castListActions: CastListActions): CastListViewState =
        when (castListActions) {
            is CastListActions.Request -> state.copy(isLoading = true, errorMessage = null)
            is CastListActions.Error -> state.copy(
                errorMessage = castListActions.errorMessage,
                isLoading = false
            )
            is CastListActions.Success -> state.copy(
                isLoading = false,
                errorMessage = null,
                data = castListActions.data
            )
            is CastListActions.SetTitle -> state.copy(title = castListActions.title)
        }
}