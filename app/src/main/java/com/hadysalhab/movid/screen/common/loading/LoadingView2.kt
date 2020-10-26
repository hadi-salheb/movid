package com.hadysalhab.movid.screen.common.loading

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class LoadingView2(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) : BaseViewMvc() {
    init {
        setRootView(layoutInflater.inflate(R.layout.component_loading_2, viewGroup, false))
    }

}