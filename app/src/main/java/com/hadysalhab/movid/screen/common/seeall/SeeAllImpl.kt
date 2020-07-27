package com.hadysalhab.movid.screen.common.seeall

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R

class SeeAllImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : SeeAll() {
    init {
        setRootView(layoutInflater.inflate(R.layout.component_see_all, parent, false))
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onSeeAllClicked()
            }
        }
    }
}