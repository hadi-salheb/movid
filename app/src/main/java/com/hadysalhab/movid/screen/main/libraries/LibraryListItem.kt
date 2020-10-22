package com.hadysalhab.movid.screen.main.libraries

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class LibraryListItem(layoutInflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<LibraryListItem.Listener>() {
    interface Listener {
        fun onLibraryListItemClick(library: Library)
    }

    private val libraryTV: TextView
    private lateinit var library: Library

    init {
        setRootView(layoutInflater.inflate(R.layout.component_library_list_item, parent, false))
        libraryTV = findViewById(R.id.library_text)
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onLibraryListItemClick(this.library)
            }
        }
    }

    fun displayLibrary(library: Library) {
        this.library = library
        libraryTV.text = library.libraryName
    }
}