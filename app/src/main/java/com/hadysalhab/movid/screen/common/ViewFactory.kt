package com.hadysalhab.movid.screen.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.screen.authentication.onboarding.OnBoardingViewImpl
import com.hadysalhab.movid.screen.common.cardgroup.CastsView
import com.hadysalhab.movid.screen.common.cardgroup.MoviesView
import com.hadysalhab.movid.screen.common.cast.CastCardImpl
import com.hadysalhab.movid.screen.common.fragmentframe.FragmentFrameView
import com.hadysalhab.movid.screen.common.movies.MovieCardImpl
import com.hadysalhab.movid.screen.common.rating.Rating
import com.hadysalhab.movid.screen.common.seeall.SeeAllImpl
import com.hadysalhab.movid.screen.main.MainViewImpl
import com.hadysalhab.movid.screen.main.featured.FeaturedViewImpl
import com.hadysalhab.movid.screen.main.moviedetail.FactView
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailView
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailViewImpl
import com.hadysalhab.movid.screen.common.movies.MovieListItemImpl
import com.hadysalhab.movid.screen.common.reviews.ReviewListItem
import com.hadysalhab.movid.screen.common.reviews.ReviewListItemImpl
import com.hadysalhab.movid.screen.main.movielist.MovieListViewImpl
import com.hadysalhab.movid.screen.main.reviews.ReviewListView
import com.hadysalhab.movid.screen.main.reviews.ReviewListViewImpl

class ViewFactory(private val layoutInflater: LayoutInflater) {
    fun getFragmentFrameView(parent: ViewGroup?) = FragmentFrameView(layoutInflater, parent)
    fun getLauncherView(parent: ViewGroup?) = OnBoardingViewImpl(layoutInflater, parent)
    fun getMainView(parent: ViewGroup?) = MainViewImpl(layoutInflater, parent)

    fun getFeaturedView(container: ViewGroup?) = FeaturedViewImpl(layoutInflater, container, this)
    fun getMovieCard(parent: ViewGroup?) =
        MovieCardImpl(
            layoutInflater,
            parent,
            this
        )

    fun getSeeAll(parent: ViewGroup?) =
        SeeAllImpl(
            layoutInflater,
            parent
        )

    fun getMoviesView(parent: ViewGroup?) =
        MoviesView(
            layoutInflater,
            parent,
            this
        )

    fun getMovieListItemView(parent: ViewGroup?) =
        MovieListItemImpl(layoutInflater, parent, this)

    fun getMovieListView(parent: ViewGroup?) = MovieListViewImpl(layoutInflater, parent, this)

    fun getCastsView(parent: ViewGroup?) =
        CastsView(
            layoutInflater,
            parent,
            this
        )

    fun getMovieDetailView(container: ViewGroup?): MovieDetailView =
        MovieDetailViewImpl(layoutInflater, container, this)

    fun getFactView(parent: ViewGroup?) = FactView(layoutInflater, parent)
    fun getCastCard(parent: ViewGroup?) = CastCardImpl(layoutInflater, parent)
    fun getRatingView(parent: ViewGroup?): Rating = Rating(layoutInflater, parent)
    fun getReviewListItem(parent: ViewGroup?): ReviewListItem =
        ReviewListItemImpl(layoutInflater, parent)

    fun getReviewsView(parent: ViewGroup?): ReviewListView =
        ReviewListViewImpl(layoutInflater, parent, this)
}
