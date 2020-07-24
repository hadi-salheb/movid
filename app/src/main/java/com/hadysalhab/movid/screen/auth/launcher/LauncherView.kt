package com.hadysalhab.movid.screen.auth.launcher

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class LauncherView : BaseObservableViewMvc<LauncherView.Listener>() {
    interface Listener {
        fun onLoginClicked(username: String, password: String)
        fun onSignUpClicked()
    }

    abstract fun showProgressState()
    abstract fun hideProgressState()
    abstract fun showIdleScreen()
}