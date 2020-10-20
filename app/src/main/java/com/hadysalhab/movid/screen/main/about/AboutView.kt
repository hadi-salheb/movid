package com.hadysalhab.movid.screen.main.about

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class AboutView(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    BaseObservableViewMvc<AboutView.Listener>() {
    interface Listener

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_about, parent, false))

    }


}