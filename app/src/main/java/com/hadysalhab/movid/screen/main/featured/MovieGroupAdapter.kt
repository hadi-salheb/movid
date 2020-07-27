package com.hadysalhab.movid.screen.main.featured

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.screen.common.ViewFactory

class MovieGroupAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    ListAdapter<MovieGroup, MovieGroupViewHolder>(DIFF_CALLBACK), MovieGroupItemView.Listener {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeMoreClicked(movieGroupType: MovieGroupType)
    }

    lateinit var movieGroupType: MovieGroupType

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieGroup>() {
            override fun areItemsTheSame(oldItem: MovieGroup, newItem: MovieGroup): Boolean {
                return oldItem.movieGroupType == newItem.movieGroupType
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
        this.movieGroupType = movieGroup.movieGroupType
        holder.bind(movieGroup)
    }

    override fun onMovieCardClicked(movieID: Int) {
        listener.onMovieCardClicked(movieID)
    }

    override fun onSeeAllClicked() {
        listener.onSeeMoreClicked(this.movieGroupType)
    }
}