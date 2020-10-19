package com.hadysalhab.movid.screen.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.account.AccountResponse


sealed class AccountActions {
    object Request : AccountActions()
    data class Success(val accountResponse: AccountResponse) : AccountActions()
}


class AccountScreenStateManager {
    private val stateLiveData = MutableLiveData<AccountViewState>()
    private var state: AccountViewState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun setInitialStateAndReturn(initialState: AccountViewState): LiveData<AccountViewState> {
        stateLiveData.value = initialState
        return stateLiveData
    }

    fun dispatch(action: AccountActions) {
        state = accountStateReducer(action)
    }

    private fun accountStateReducer(action: AccountActions): AccountViewState = when (action) {
        AccountActions.Request -> state.copy(loading = true)
        is AccountActions.Success -> state.copy(
            loading = false,
            accountResponse = action.accountResponse
        )
    }
}