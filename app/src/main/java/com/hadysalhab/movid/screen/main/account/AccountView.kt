package com.hadysalhab.movid.screen.main.account

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class AccountViewState(
    val loading: Boolean = false,
    val accountResponse: AccountResponse? = null
)

abstract class AccountView : BaseObservableViewMvc<AccountView.Listener>() {
    interface Listener
}