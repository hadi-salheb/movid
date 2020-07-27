package com.hadysalhab.movid.screen.common.movies

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.seeall.SeeAll

sealed class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(movie: Movie) {
        // Override in subclass if needed.
    }

    class MovieCardViewHolder(
        private val movieCard: MovieCard
    ) :
        MovieViewHolder(movieCard.getRootView()) {
        //template method
        override fun bind(movie: Movie) {
            super.bind(movie)
            movieCard.displayMovie(movie)
        }
    }

    class SeeAllViewHolder(
        seeAll: SeeAll
    ) : MovieViewHolder(seeAll.getRootView())

    companion object {
        fun createMovieCard(
            movieCard: MovieCard
        ) =
            MovieCardViewHolder(
                movieCard
            )

        fun createSeeAll(seeAll: SeeAll) =
            SeeAllViewHolder(
                seeAll
            )
    }
}