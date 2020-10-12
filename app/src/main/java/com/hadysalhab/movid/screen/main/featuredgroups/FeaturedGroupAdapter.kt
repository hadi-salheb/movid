package com.hadysalhab.movid.screen.main.featuredgroups

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.common.utils.convertDpToPixel
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.cardgroup.MoviesView

class FeaturedGroupAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    ListAdapter<MoviesResponse, FeaturedGroupViewHolder>(DIFF_CALLBACK), MoviesView.Listener {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeMoreClicked(groupType: GroupType)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MoviesResponse>() {
            // check if two object are the same (id,tag...)
            override fun areItemsTheSame(
                oldItem: MoviesResponse,
                newItem: MoviesResponse
            ): Boolean {
                return oldItem.tag == newItem.tag
            }

            // check if two object have the same visual presentation (only called if two items are the same)
            override fun areContentsTheSame(
                oldItem: MoviesResponse,
                newItem: MoviesResponse
            ): Boolean {
                return oldItem.movies == newItem.movies
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedGroupViewHolder {
        val view = viewFactory.getMoviesView(parent)
        view.registerListener(this)
        val padding = convertDpToPixel(8, parent.context)
        view.getRootView().setPadding(0, padding, 0, padding)
        return FeaturedGroupViewHolder(view)
    }

    // only called if areContentsTheSame = false
    override fun onBindViewHolder(holder: FeaturedGroupViewHolder, position: Int) {
        val movieGroup = getItem(position)
        holder.bind(movieGroup)
    }


    override fun onMovieCardClicked(movieID: Int) {
        listener.onMovieCardClicked(movieID)
    }

    override fun onMovieSeeAllClicked(groupType: GroupType) {
        listener.onSeeMoreClicked(groupType)
    }
}