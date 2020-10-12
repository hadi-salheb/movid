package com.hadysalhab.movid.screen.common.movielist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movies.MovieListItem
import com.hadysalhab.movid.screen.common.paginationerror.PaginationError


class MovieListAdapter(private val listener: Listener, private val viewFactory: ViewFactory) :
    androidx.recyclerview.widget.ListAdapter<Movie, MovieListViewHolder>(DIFF_CALLBACK),
    MovieListItem.Listener, PaginationError.Listener {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
        fun onPaginationErrorClicked()
    }

    val LOADING = 0
    val MOVIE = 1
    val ERROR = 2

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder =
        when (viewType) {
            MOVIE -> {
                val view = viewFactory.getMovieListItemView(parent)
                view.registerListener(this)
                MovieListViewHolder.MovieViewHolder(view)
            }
            LOADING -> {
                val view = viewFactory.getLoadingView(parent)
                MovieListViewHolder.LoadingViewHolder(view)
            }
            ERROR -> {
                val view = viewFactory.getPaginationErrorView(parent)
                view.registerListener(this)
                MovieListViewHolder.PaginationErrorViewHolder(view)
            }
            else -> {
                throw RuntimeException("Unsupported viewType $viewType")
            }
        }

    override fun getItemViewType(position: Int): Int = when (getItem(position).title) {
        "LOADING" -> LOADING
        "ERROR" -> ERROR
        else -> MOVIE
    }

    override fun onMovieItemClicked(movieID: Int) {
        listener.onMovieItemClicked(movieID)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onPaginationErrorClick() {
        listener.onPaginationErrorClicked()
    }
}