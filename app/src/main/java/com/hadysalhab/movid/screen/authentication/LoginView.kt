package com.hadysalhab.movid.screen.authentication

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class LoginView : BaseObservableViewMvc<LoginView.Listener>() {
    interface Listener {
        fun onLoginClicked(username: String, password: String)
        fun onSignUpClicked()
        fun onBrowseClicked()
    }

    abstract fun showProgressState()
    abstract fun hideProgressState()
    abstract fun showIdleScreen()
}