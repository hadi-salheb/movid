package com.hadysalhab.movid.screen.main.castlist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.people.People


class PeopleListAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    androidx.recyclerview.widget.ListAdapter<People, PeopleListItemViewHolder>(DIFF_CALLBACK) {
    interface Listener

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<People>() {
            override fun areItemsTheSame(oldItem: People, newItem: People): Boolean {
                return oldItem.peopleId == newItem.peopleId
            }

            override fun areContentsTheSame(oldItem: People, newItem: People): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleListItemViewHolder {
        val view = viewFactory.getPeopleListItem(parent)
        return PeopleListItemViewHolder.PeopleViewHolder(view)
    }


    override fun onBindViewHolder(holder: PeopleListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}