package com.hadysalhab.movid.screen.common.dialogs.ratedialog

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class RateFormViewState(
    val rate: Double?,
    val title: String
)

abstract class RateFormView : BaseObservableViewMvc<RateFormView.Listener>() {
    abstract fun handleState(rateFormViewState: RateFormViewState)

    interface Listener {
        fun onNegativeBtnClicked()
        fun onPositiveBtnClicked()
        fun onRateChange(rate: Double)
    }
}