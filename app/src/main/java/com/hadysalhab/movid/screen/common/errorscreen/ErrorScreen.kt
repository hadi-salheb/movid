package com.hadysalhab.movid.screen.common.errorscreen

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class ErrorScreen : BaseObservableViewMvc<ErrorScreen.Listener>() {
    interface Listener {
        fun onRetryClicked()
    }

    abstract fun displayErrorMessage(errorMessage: String)
}