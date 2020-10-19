package com.hadysalhab.movid.screen.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCase
import timber.log.Timber
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase,
    private val accountScreenStateManager: AccountScreenStateManager
) : ViewModel(), GetAccountDetailsUseCase.Listener {
    val screenState: LiveData<AccountViewState>
    private val dispatch = accountScreenStateManager::dispatch

    init {
        getAccountDetailsUseCase.registerListener(this)
        screenState =
            accountScreenStateManager.setInitialStateAndReturn(AccountViewState(loading = true))
        getAccountDetailsUseCase.getAccountDetailsUseCase()
    }


    //UseCase Response------------------------------------------------------------------------------
    override fun getAccountDetailsSuccess(accountDetail: AccountResponse) {
        Timber.d("AccountDetail: $accountDetail")
        dispatch(AccountActions.Success(accountDetail))
    }


    override fun onCleared() {
        super.onCleared()
        getAccountDetailsUseCase.unregisterListener(this)
    }
}