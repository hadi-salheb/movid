package com.hadysalhab.movid.screen.common.dialogs.ratedialog

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class RateViewState(
    val isLoading: Boolean,
    val rate: Double?,
    val title: String
)

abstract class RateDialogScreen : BaseObservableViewMvc<RateDialogScreen.Listener>() {
    interface Listener {
        fun onNegativeBtnClicked()
        fun onPositiveBtnClicked()
        fun onRateChange(rate: Double)
    }

    abstract fun handleState(rateViewState: RateViewState)
}