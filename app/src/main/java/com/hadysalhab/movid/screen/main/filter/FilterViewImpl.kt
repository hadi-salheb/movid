package com.hadysalhab.movid.screen.main.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R

class FilterViewImpl(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) : FilterView() {
    init {
        setRootView(layoutInflater.inflate(R.layout.layout_filter, viewGroup, false))
    }
}