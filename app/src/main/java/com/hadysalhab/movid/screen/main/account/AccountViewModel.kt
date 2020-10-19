package com.hadysalhab.movid.screen.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCase
import com.hadysalhab.movid.authentication.SignOutUseCase
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase,
    private val accountScreenStateManager: AccountScreenStateManager,
    private val signOutUseCase: SignOutUseCase
) : ViewModel(), GetAccountDetailsUseCase.Listener {
    val screenState: LiveData<AccountViewState>
    private val dispatch = accountScreenStateManager::dispatch
    private lateinit var accountDetail: AccountResponse
    private var isSigninOut = false
    init {
        getAccountDetailsUseCase.registerListener(this)
        screenState =
            accountScreenStateManager.setInitialStateAndReturn(AccountViewState(loading = true))
        getAccountDetailsUseCase.getAccountDetailsUseCase()
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
        if (!isSigninOut) {
            isSigninOut = true
            signOutUseCase.signOutUser(accountDetail)
        }
    }
}