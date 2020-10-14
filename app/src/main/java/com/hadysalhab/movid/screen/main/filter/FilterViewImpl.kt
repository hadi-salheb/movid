package com.hadysalhab.movid.screen.main.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout

class FilterViewImpl(
    layoutInflater: LayoutInflater,
    viewGroup: ViewGroup?,
    viewFactory: ViewFactory
) : FilterView() {
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_filter, viewGroup, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.setToolbarTitle("FILTER")
    }
}