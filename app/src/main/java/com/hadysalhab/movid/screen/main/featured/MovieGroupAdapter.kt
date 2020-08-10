package com.hadysalhab.movid.screen.main.featured

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.cardgroup.CardGroupView

class MovieGroupAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    ListAdapter<MovieGroup, MovieGroupViewHolder>(DIFF_CALLBACK), CardGroupView.Listener {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeMoreClicked(groupType: GroupType)
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieGroup>() {
            override fun areItemsTheSame(oldItem: MovieGroup, newItem: MovieGroup): Boolean {
                return oldItem.groupType == newItem.groupType
            }

            override fun areContentsTheSame(oldItem: MovieGroup, newItem: MovieGroup): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieGroupViewHolder {
        val view = viewFactory.getMovieGroupView(parent)
        view.registerListener(this)
        return MovieGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieGroupViewHolder, position: Int) {
        val movieGroup = getItem(position)
        holder.bind(movieGroup)
    }

    override fun onCardClicked(cardID: Int) {
        listener.onMovieCardClicked(cardID)
    }

    override fun onSeeAllClicked(cardGroupType: GroupType) {
        listener.onSeeMoreClicked(cardGroupType)
    }


}