package com.hadysalhab.movid.screen.main.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.screen.common.ViewFactory

class GenreAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    ListAdapter<Genre, GenreViewHolder>(DIFF_CALLBACK),
    GenreListItem.Listener {
    interface Listener

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Genre>() {
            override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = viewFactory.getGenreListItem(parent)
        view.registerListener(this)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}