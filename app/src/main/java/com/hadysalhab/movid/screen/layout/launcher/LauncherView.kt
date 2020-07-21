package com.hadysalhab.movid.screen.layout.launcher

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class LauncherView : BaseObservableViewMvc<LauncherView.Listener>() {
    interface Listener {
        fun onLoginClicked()
        fun onSignUpClicked()
    }
}