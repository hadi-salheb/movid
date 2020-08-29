package com.hadysalhab.movid.screen.common.cardgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movies.MovieCard
import com.hadysalhab.movid.screen.common.seeall.SeeAll

// Template Method Design Pattern
class MoviesView(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : CardGroupViewImpl<DataGroup<Movie>, MoviesView.Listener>(layoutInflater, parent),
    MovieCard.Listener, SeeAll.Listener {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked(groupType: GroupType)
    }


    private fun createMovieCardAndAppend(movies: List<Movie>) {
        linearLayout.removeAllViews()
        movies.take(5).forEach { movie ->
            val movieCard = viewFactory.getMovieCard(linearLayout)
            movieCard.registerListener(this)
            movieCard.displayMovie(movie)
            linearLayout.addView(movieCard.getRootView())
        }
        if (movies.size > 5) {
            displaySeeAll()
        }
    }

    private fun displaySeeAll() {
        val seeAll = viewFactory.getSeeAll(linearLayout)
        seeAll.registerListener(this)
        linearLayout.addView(seeAll.getRootView())
    }

    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieCardClicked(movieID)
        }
    }

    override fun onSeeAllClicked() {
        listeners.forEach {
            it.onSeeAllClicked(this.groupType)
        }
    }

    override fun displayCardGroup(data: DataGroup<Movie>) {
        createMovieCardAndAppend(data.data)
    }
}