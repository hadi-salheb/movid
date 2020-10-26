package com.hadysalhab.movid.screen.main.account

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class AccountViewState(
    val loading: Boolean = false,
    val accountResponse: AccountResponse? = null
)

abstract class AccountView : BaseObservableViewMvc<AccountView.Listener>() {
    abstract fun handleState(state: AccountViewState)
    abstract fun toggleDarkModeSwitch(darkTheme: Boolean)

    interface Listener {
        fun onSignOutClick()
        fun onAboutClick()
        fun onContactDevClicked()
        fun onLibrariesClicked()
        fun onRateClicked()
        fun onShareClicked()
        fun onDarkModeCheckedChanged(checked: Boolean)
        fun onPrivacyPolicyClicked()
    }
}