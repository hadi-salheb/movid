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
        return if (viewType == 0) {
            val view = viewFactory.getListHeader(parent)
            view.setText("Browse Genres")
            GenreHeaderViewHolder(view)
        } else {
            val view = viewFactory.getGenreListItem(parent)
            view.registerListener(this)
            GenreItemViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun getItem(position: Int): Genre {
        return currentList[position - 1]
    }

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        if (holder is GenreItemViewHolder) {
            holder.bind(getItem(position))
        }
    }
}