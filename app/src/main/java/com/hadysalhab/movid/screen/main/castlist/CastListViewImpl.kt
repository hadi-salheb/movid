package com.hadysalhab.movid.screen.main.castlist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory

class CastListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : CastListView() {
    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_title_toolbar, parent, false))
    }
}