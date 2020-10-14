package com.hadysalhab.movid.screen.common.movielist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.loading.LoadingView
import com.hadysalhab.movid.screen.common.movies.MovieListItem
import com.hadysalhab.movid.screen.common.paginationerror.PaginationError


sealed class MovieListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(movie: Movie) {}
    class MovieViewHolder(private val movieListItem: MovieListItem) :
        MovieListViewHolder(movieListItem.getRootView()) {
        override fun bind(movie: Movie) {
            super.bind(movie)
            movieListItem.displayMovie(movie)
        }
    }

    class LoadingViewHolder(loadingView: LoadingView) :
        MovieListViewHolder(loadingView.getRootView())

    class PaginationErrorViewHolder(paginationError: PaginationError) :
        MovieListViewHolder(paginationError.getRootView())
}