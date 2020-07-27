package com.hadysalhab.movid.screen.authentication.onboarding

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class OnBoardingView : BaseObservableViewMvc<OnBoardingView.Listener>() {
    interface Listener {
        fun onLoginClicked(username: String, password: String)
        fun onSignUpClicked()
    }

    abstract fun showProgressState()
    abstract fun hideProgressState()
    abstract fun showIdleScreen()
}