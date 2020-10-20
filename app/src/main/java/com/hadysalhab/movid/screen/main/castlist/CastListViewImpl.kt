package com.hadysalhab.movid.screen.main.castlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout

class CastListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : CastListView(), MenuToolbarLayout.Listener {
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_title_toolbar, parent, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.showBackArrow()
        menuToolbarLayout.registerListener(this)
    }

    override fun onOverflowMenuIconClick() {

    }

    override fun onBackArrowClicked() {
        listeners.forEach { it.onBackArrowClicked() }
    }
}