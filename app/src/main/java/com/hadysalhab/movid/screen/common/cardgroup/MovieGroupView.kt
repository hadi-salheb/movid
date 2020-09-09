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

    override fun displayCardGroup(data: DataGroup<Movie>, maxNum: Int?) {
        createMovieCardAndAppend(data.data, maxNum)
    }


    private fun createMovieCardAndAppend(movies: List<Movie>, maxNum: Int?) {
        linearLayout.removeAllViews()
        if (maxNum == null) {
            movies.forEach { movie ->
                createMovieCard(movie)
            }
        } else {
            movies.take(maxNum).forEach { movie ->
                createMovieCard(movie)
            }
            if (movies.size > maxNum) {
                displaySeeAll()
            }
        }
    }

    private fun createMovieCard(movie: Movie) {
        val movieCard = viewFactory.getMovieCard(linearLayout)
        movieCard.registerListener(this)
        movieCard.displayMovie(movie)
        linearLayout.addView(movieCard.getRootView())
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

}