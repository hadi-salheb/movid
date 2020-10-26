package com.hadysalhab.movid.screen.common.dialogs.ratedialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.loading.LoadingView2


class RateDialogScreenImpl(
    layoutInflater: LayoutInflater,
    viewGroup: ViewGroup?,
    viewFactory: ViewFactory
) : RateDialogScreen(),
    RateFormView.Listener {
    private val rateDialogPlaceHolder: FrameLayout
    private val rateFormView: RateFormView
    private val loading: LoadingView2

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_rate_dialog, viewGroup, false))
        rateDialogPlaceHolder = findViewById(R.id.layout_rate_placeholder)
        rateFormView = viewFactory.getRateFormView(rateDialogPlaceHolder)
        loading = viewFactory.getLoading2View(rateDialogPlaceHolder)
        rateFormView.registerListener(this)
    }

    override fun handleState(rateViewState: RateViewState) {
        with(rateViewState) {
            if (isLoading) {
                rateDialogPlaceHolder.removeAllViews()
                rateDialogPlaceHolder.addView(loading.getRootView())
            } else {
                rateDialogPlaceHolder.removeAllViews()
                rateDialogPlaceHolder.addView(rateFormView.getRootView())
                rateFormView.handleState(RateFormViewState(rate, title))
            }
        }
    }

    override fun onNegativeBtnClicked() {
        listeners.forEach {
            it.onNegativeBtnClicked()
        }
    }

    override fun onPositiveBtnClicked() {
        listeners.forEach {
            it.onPositiveBtnClicked()
        }
    }

    override fun onRateChange(rate: Double) {
        listeners.forEach {
            it.onRateChange(rate)
        }
    }
}