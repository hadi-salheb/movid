package com.hadysalhab.movid.screen.common.loading

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class LoadingView(layoutInflater: LayoutInflater, container: ViewGroup?) : BaseViewMvc() {
    init {
        setRootView(layoutInflater.inflate(R.layout.component_loading, container, false))
    }
}