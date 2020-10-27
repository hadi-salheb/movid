package com.hadysalhab.movid.screen.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCase
import com.hadysalhab.movid.authentication.SignOutUseCase
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.constants.GUEST_SESSION_ID
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase,
    private val accountScreenStateManager: AccountScreenStateManager,
    private val signOutUseCase: SignOutUseCase,
    sharedPreferencesManager: SharedPreferencesManager
) : ViewModel(), GetAccountDetailsUseCase.Listener {
    val screenState: LiveData<AccountViewState>
    private val dispatch = accountScreenStateManager::dispatch
    private var accountDetail: AccountResponse? = null
    private var isSigningOut = false
    private val sessionId = sharedPreferencesManager.getStoredSessionId()

    init {
        getAccountDetailsUseCase.registerListener(this)
        if (sessionId == GUEST_SESSION_ID) {
            screenState = accountScreenStateManager.setInitialStateAndReturn(
                AccountViewState(
                    loading = false,
                    accountResponse = null
                )
            )
        } else {
            screenState =
                accountScreenStateManager.setInitialStateAndReturn(AccountViewState(loading = true))
            getAccountDetailsUseCase.getAccountDetailsUseCase()
        }
    }


    //UseCase Response------------------------------------------------------------------------------
    override fun getAccountDetailsSuccess(accountDetail: AccountResponse) {
        this.accountDetail = accountDetail
        dispatch(AccountActions.Success(accountDetail))
    }


    override fun onCleared() {
        super.onCleared()
        getAccountDetailsUseCase.unregisterListener(this)
    }

    fun signOutClick() {
        //prevent double tap bug
        if (!isSigningOut) {
            isSigningOut = true
            signOutUseCase.signOutUser(accountDetail)
        }
    }
}