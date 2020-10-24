package com.hadysalhab.movid.screen.main.castlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.screen.common.people.People

sealed class PeopleListActions {
    object Request : PeopleListActions()
    data class Error(val errorMessage: String?) : PeopleListActions()
    data class Success(val data: List<People>) : PeopleListActions()
    data class SetTitle(val title: String) : PeopleListActions()
}

class PeopleListScreenStateManager {
    private val stateLiveData = MutableLiveData<PeopleListViewState>()

    fun setInitialStateAndReturn(initialState: PeopleListViewState): LiveData<PeopleListViewState> {
        stateLiveData.value = initialState
        return stateLiveData
    }

    private var state: PeopleListViewState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(peopleListActions: PeopleListActions) {
        state = castListReducer(peopleListActions)
    }

    private fun castListReducer(peopleListActions: PeopleListActions): PeopleListViewState =
        when (peopleListActions) {
            is PeopleListActions.Request -> state.copy(isLoading = true, errorMessage = null)
            is PeopleListActions.Error -> state.copy(
                errorMessage = peopleListActions.errorMessage,
                isLoading = false
            )
            is PeopleListActions.Success -> state.copy(
                isLoading = false,
                errorMessage = null,
                data = peopleListActions.data
            )
            is PeopleListActions.SetTitle -> state.copy(title = peopleListActions.title)
        }
}