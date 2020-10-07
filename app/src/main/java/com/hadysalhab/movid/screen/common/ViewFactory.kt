package com.hadysalhab.movid.screen.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.screen.authentication.onboarding.OnBoardingViewImpl
import com.hadysalhab.movid.screen.common.cardgroup.CastsView
import com.hadysalhab.movid.screen.common.cardgroup.MoviesView
import com.hadysalhab.movid.screen.common.cast.CastCardImpl
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreenImpl
import com.hadysalhab.movid.screen.common.fragmentframe.FragmentFrameView
import com.hadysalhab.movid.screen.common.listheader.ListHeader
import com.hadysalhab.movid.screen.common.loading.LoadingView
import com.hadysalhab.movid.screen.common.movielist.MovieListViewImpl
import com.hadysalhab.movid.screen.common.movies.MovieCardImpl
import com.hadysalhab.movid.screen.common.movies.MovieListItemImpl
import com.hadysalhab.movid.screen.common.rating.Rating
import com.hadysalhab.movid.screen.common.reviews.ReviewListItem
import com.hadysalhab.movid.screen.common.reviews.ReviewListItemImpl
import com.hadysalhab.movid.screen.common.seeall.SeeAllImpl
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.hadysalhab.movid.screen.main.MainViewImpl
import com.hadysalhab.movid.screen.main.discover.DiscoverView
import com.hadysalhab.movid.screen.main.discover.DiscoverViewImpl
import com.hadysalhab.movid.screen.main.featured.FeaturedScreenImpl
import com.hadysalhab.movid.screen.main.moviedetail.FactView
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailScreen
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailScreenImpl
import com.hadysalhab.movid.screen.main.reviews.ReviewListView
import com.hadysalhab.movid.screen.main.reviews.ReviewListViewImpl
import com.hadysalhab.movid.screen.main.search.*

class ViewFactory(private val layoutInflater: LayoutInflater) {
    fun getFragmentFrameView(parent: ViewGroup?) = FragmentFrameView(layoutInflater, parent)
    fun getLauncherView(parent: ViewGroup?) = OnBoardingViewImpl(layoutInflater, parent)
    fun getMainView(parent: ViewGroup?) = MainViewImpl(layoutInflater, parent)

    fun getFeaturedView(container: ViewGroup?) = FeaturedScreenImpl(layoutInflater, container, this)
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

    fun getMovieDetailView(container: ViewGroup?): MovieDetailScreen =
        MovieDetailScreenImpl(layoutInflater, container, this)

    fun getFactView(parent: ViewGroup?) = FactView(layoutInflater, parent)
    fun getCastCard(parent: ViewGroup?) = CastCardImpl(layoutInflater, parent)
    fun getRatingView(parent: ViewGroup?): Rating = Rating(layoutInflater, parent)
    fun getReviewListItem(parent: ViewGroup?): ReviewListItem =
        ReviewListItemImpl(layoutInflater, parent)

    fun getReviewsView(parent: ViewGroup?): ReviewListView =
        ReviewListViewImpl(layoutInflater, parent, this)

    fun getMenuToolbarLayout(parent: ViewGroup?) = MenuToolbarLayout(layoutInflater, parent)
    fun getSearchView(parent: ViewGroup?): SearchView = SearchViewImpl(layoutInflater, parent, this)
    fun getGenreListItem(parent: ViewGroup?): GenreListItem =
        GenreListItemImpl(layoutInflater, parent)

    fun getGenreList(parent: ViewGroup?): GenreList = GenreList(layoutInflater, parent, this)
    fun getListHeader(parent: ViewGroup?): ListHeader = ListHeader(layoutInflater, parent)
    fun getDiscoverView(parent: ViewGroup?): DiscoverView =
        DiscoverViewImpl(layoutInflater, parent, this)

    fun getErrorScreen(parent: ViewGroup?) = ErrorScreenImpl(layoutInflater, parent)
    fun getLoadingView(parent: ViewGroup?) = LoadingView(layoutInflater, parent)
}
