package com.hadysalhab.movid.screen.main.libraries

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class LibrariesView(layoutInflater: LayoutInflater, parent: ViewGroup?, viewFactory: ViewFactory) :
    BaseObservableViewMvc<LibrariesView.Listener>(), LibrariesAdapter.Listener,
    MenuToolbarLayout.Listener {
    interface Listener {
        fun onLibraryListItemClicked(library: Library)
        fun onBackArrowClicked()
    }

    private val recyclerView: RecyclerView
    private val adapter: LibrariesAdapter
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_libraries, parent, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar).also {
            it.showBackArrow()
            it.setToolbarTitle("LIBRARIES")
        }
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.registerListener(this)
        recyclerView = findViewById(R.id.libraries_rv)
        adapter = LibrariesAdapter(this, viewFactory)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@LibrariesView.adapter
            setHasFixedSize(true)
        }
        adapter.submitList(Library.values().toList())
    }

    override fun onLibraryListItemClicked(library: Library) {
        listeners.forEach {
            it.onLibraryListItemClicked(library)
        }
    }

    override fun onOverflowMenuIconClick() {

    }

    override fun onBackArrowClicked() {
        listeners.forEach {
            it.onBackArrowClicked()
        }
    }
}