package com.hadysalhab.movid.screen.main.movielist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory

class MovieListAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    androidx.recyclerview.widget.ListAdapter<Movie, MovieListViewHolder>(DIFF_CALLBACK),
    MovieListItemView.Listener {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        val view = viewFactory.getMovieListItemView(parent)
        view.registerListener(this)
        return MovieListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onMovieItemClicked(movieID: Int) {
        listener.onMovieItemClicked(movieID)
    }
}