package com.hadysalhab.movid.screen.main.featured

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.movies.MovieCard
import com.hadysalhab.movid.screen.common.seeall.SeeAll

class MovieGroupItemViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) :
    MovieGroupItemView(), MovieCard.Listener, SeeAll.Listener {
    private val linearLayout: LinearLayout
    private lateinit var movieGroupType: MovieGroupType
    private val groupTitle: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_group, parent, false))
        linearLayout = findViewById(R.id.movie_linear_layout)
        groupTitle = findViewById(R.id.group_title)
    }


    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieCardClicked(movieID)
        }
    }

    override fun onSeeAllClicked() {
        listeners.forEach {
            it.onSeeAllClicked(this.movieGroupType)
        }
    }


    override fun displayMovieGroup(movieGroup: MovieGroup) {
        this.movieGroupType = movieGroup.movieGroupType
        groupTitle.text = movieGroup.movieGroupType.value.toUpperCase().split("_").joinToString(" ")
        createMovieCardAndAppend(movieGroup.movies)
    }

    private fun createMovieCardAndAppend(movies: List<Movie>) {
        movies.take(5).forEach { movie ->
            val movieCard = viewFactory.getMovieCard(linearLayout)
            movieCard.registerListener(this)
            movieCard.displayMovie(movie)
            linearLayout.addView(movieCard.getRootView())
        }
        val seeAll = viewFactory.getSeeAll(linearLayout)
        seeAll.registerListener(this)
        linearLayout.addView(seeAll.getRootView())


    }
}