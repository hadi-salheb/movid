package com.hadysalhab.movid.screen.main.castlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.screen.common.people.People


sealed class PeopleListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(people: People) {}
    class PeopleViewHolder(private val peopleListItem: PeopleListItem) :
        PeopleListItemViewHolder(peopleListItem.getRootView()) {
        override fun bind(people: People) {
            super.bind(people)
            peopleListItem.displayPeople(people)
        }
    }
}