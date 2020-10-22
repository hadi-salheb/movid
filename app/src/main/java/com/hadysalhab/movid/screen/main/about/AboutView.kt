package com.hadysalhab.movid.screen.main.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class AboutView(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    BaseObservableViewMvc<AboutView.Listener>(), MenuToolbarLayout.Listener {
    interface Listener {
        fun onBackArrowClicked()
    }

    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_about, parent, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar).also {
            it.showBackArrow()
            it.setToolbarTitle("ABOUT")
        }
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.registerListener(this)

    }

    override fun onOverflowMenuIconClick() {

    }

    override fun onBackArrowClicked() {
        listeners.forEach {
            it.onBackArrowClicked()
        }
    }


}