package com.hadysalhab.movid.screen.main.account

import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCase
import timber.log.Timber
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase
) : ViewModel(), GetAccountDetailsUseCase.Listener {
    init {
        getAccountDetailsUseCase.registerListener(this)
        getAccountDetailsUseCase.getAccountDetailsUseCase()
    }


    //UseCase Response------------------------------------------------------------------------------
    override fun getAccountDetailsSuccess(accountDetail: AccountResponse) {
        Timber.d("AccountDetail: $accountDetail")
    }


    override fun onCleared() {
        super.onCleared()
        getAccountDetailsUseCase.unregisterListener(this)
    }
}