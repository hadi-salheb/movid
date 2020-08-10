package com.hadysalhab.movid.screen.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.screen.authentication.onboarding.OnBoardingViewImpl
import com.hadysalhab.movid.screen.common.movies.MovieCardImpl
import com.hadysalhab.movid.screen.common.seeall.SeeAllImpl
import com.hadysalhab.movid.screen.common.fragmentframe.FragmentFrameView
import com.hadysalhab.movid.screen.main.MainViewImpl
import com.hadysalhab.movid.screen.main.featured.FeaturedViewImpl
import com.hadysalhab.movid.screen.main.featured.CardGroupViewImpl
import com.hadysalhab.movid.screen.main.moviedetail.FactView
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailView
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailViewImpl

class ViewFactory(private val layoutInflater: LayoutInflater) {
    fun getFragmentFrameView(parent: ViewGroup?) = FragmentFrameView(layoutInflater, parent)
    fun getLauncherView(parent: ViewGroup?) = OnBoardingViewImpl(layoutInflater, parent)
    fun getMainView(parent: ViewGroup?) = MainViewImpl(layoutInflater, parent)

    fun getFeaturedView(container: ViewGroup?) = FeaturedViewImpl(layoutInflater, container, this)
    fun getMovieCard(parent: ViewGroup?) =
        MovieCardImpl(
            layoutInflater,
            parent
        )

    fun getSeeAll(parent: ViewGroup?) =
        SeeAllImpl(
            layoutInflater,
            parent
        )
    fun getMovieGroupView(parent: ViewGroup?) =
        CardGroupViewImpl(layoutInflater, parent, this)

    fun getMovieDetailView(container: ViewGroup?): MovieDetailView  = MovieDetailViewImpl(layoutInflater,container,this)
    fun getFactView(parent:ViewGroup?)  = FactView(layoutInflater,parent)

}
