package com.hadysalhab.movid.screen.main.castlist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.hadysalhab.movid.movies.Cast
import com.hadysalhab.movid.screen.common.ViewFactory


class CastListAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    androidx.recyclerview.widget.ListAdapter<Cast, CastListItemViewHolder>(DIFF_CALLBACK) {
    interface Listener

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cast>() {
            override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastListItemViewHolder {
        val view = viewFactory.getCastListItemView(parent)
        return CastListItemViewHolder.CastViewHolder(view)
    }


    override fun onBindViewHolder(holder: CastListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}