package com.hadysalhab.movid.screen.common.cardgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.cast.CastCard
import com.hadysalhab.movid.screen.common.movies.MovieCard
import com.hadysalhab.movid.screen.common.seeall.SeeAll

abstract class CardGroupViewImpl<T>(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) :
    CardGroupView(), SeeAll.Listener {
    protected val linearLayout: LinearLayout
    protected lateinit var groupType: GroupType
    protected val groupTitle: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_group, parent, false))
        linearLayout = findViewById(R.id.movie_linear_layout)
        groupTitle = findViewById(R.id.group_title)
    }

    override fun onSeeAllClicked() {
        listeners.forEach {
            it.onSeeAllClicked(this.groupType)
        }
    }

    abstract fun displayCardGroup(data: T)

    protected fun displaySeeAll() {
        val seeAll = viewFactory.getSeeAll(linearLayout)
        seeAll.registerListener(this)
        linearLayout.addView(seeAll.getRootView())
    }
}

// Template Method Design Pattern

class MoviesView(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : CardGroupViewImpl<MovieGroup>(layoutInflater, parent, viewFactory), MovieCard.Listener {

    override fun displayCardGroup(data: MovieGroup) {
        this.groupType = data.groupType
        groupTitle.text = data.groupType.value.toUpperCase().split("_").joinToString(" ")
        createMovieCardAndAppend(data.movies)
    }

    private fun createMovieCardAndAppend(movies: List<Movie>) {
        movies.take(5).forEach { movie ->
            val movieCard = viewFactory.getMovieCard(linearLayout)
            movieCard.registerListener(this)
            movieCard.displayMovie(movie)
            linearLayout.addView(movieCard.getRootView())
        }
        displaySeeAll()
    }

    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onCardClicked(movieID)
        }
    }
}

class CastsView(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : CardGroupViewImpl<CastGroup>(layoutInflater, parent, viewFactory), CastCard.Listener {

    override fun onCastCardClicked(castID: Int) {
        listeners.forEach {
            it.onCardClicked(castID)
        }
    }

    override fun displayCardGroup(data: CastGroup) {
        this.groupType = data.groupType
        groupTitle.text = data.groupType.value.toUpperCase().split("_").joinToString(" ")
        createCastCardAndAppend(data.casts)
    }

    private fun createCastCardAndAppend(casts: List<Cast>) {
        casts.take(5).forEach { cast ->
            val castCard = viewFactory.getCastCard(linearLayout)
            castCard.registerListener(this)
            castCard.displayCast(cast)
            linearLayout.addView(castCard.getRootView())
        }
        displaySeeAll()
    }

}

