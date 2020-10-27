package com.hadysalhab.movid.screen.main.icons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class IconList(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    BaseObservableViewMvc<IconList.Listener>(), IconsAdapter.Listener,
    MenuToolbarLayout.Listener {
    interface Listener {
        fun onIconTagClicked(href: String)
        fun onBackArrowClicked()
    }

    private val recyclerView: RecyclerView
    private val adapter: IconsAdapter
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_libraries, parent, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar).also {
            it.showBackArrow()
            it.setToolbarTitle("ICONS")
        }
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.registerListener(this)
        recyclerView = findViewById(R.id.libraries_rv)
        adapter = IconsAdapter(this, viewFactory)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@IconList.adapter
            setHasFixedSize(true)
        }
    }

    fun displayIconsWithDarkMode() {
        adapter.displayIconsWithDarkMode(Icon.values().toList())
    }

    fun displayIconsWithLightMode() {
        adapter.displayIconsWithLightMode(Icon.values().toList())
    }


    override fun onOverflowMenuIconClick() {

    }

    override fun onBackArrowClicked() {
        listeners.forEach {
            it.onBackArrowClicked()
        }
    }

    override fun onIconTagClicked(href: String) {
        listeners.forEach {
            it.onIconTagClicked(href)
        }
    }
}