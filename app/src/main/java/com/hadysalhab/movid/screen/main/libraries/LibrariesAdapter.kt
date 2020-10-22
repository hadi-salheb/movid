package com.hadysalhab.movid.screen.main.libraries

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.screen.common.ViewFactory

class LibrariesAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    ListAdapter<Library, LibraryViewHolder>(DIFF_CALLBACK),
    LibraryListItem.Listener {
    interface Listener {
        fun onLibraryListItemClicked(library: Library)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Library>() {
            override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean {
                return oldItem.libraryUrl == newItem.libraryUrl
            }

            override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = viewFactory.getLibraryListItem(parent)
        view.registerListener(this)
        return LibraryViewHolder(view)
    }


    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onLibraryListItemClick(library: Library) {
        listener.onLibraryListItemClicked(library)
    }
}