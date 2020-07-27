package com.hadysalhab.movid.screen.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.screen.auth.launcher.LauncherViewImpl
import com.hadysalhab.movid.screen.common.components.moviecard.MovieCardImpl
import com.hadysalhab.movid.screen.common.components.seeall.SeeAllImpl
import com.hadysalhab.movid.screen.common.fragmentframe.FragmentFrameView
import com.hadysalhab.movid.screen.main.MainViewImpl
import com.hadysalhab.movid.screen.main.featured.FeaturedViewImpl
import com.hadysalhab.movid.screen.main.featured.MovieGroupItemViewImpl

class ViewFactory(private val layoutInflater: LayoutInflater) {
    fun getFragmentFrameView(parent: ViewGroup?) = FragmentFrameView(layoutInflater, parent)
    fun getLauncherView(parent: ViewGroup?) = LauncherViewImpl(layoutInflater, parent)
    fun getMainView(parent: ViewGroup?) = MainViewImpl(layoutInflater, parent)

    fun getFeaturedView(container: ViewGroup?) = FeaturedViewImpl(layoutInflater, container, this)
    fun getMovieCard(parent: ViewGroup?) =
        MovieCardImpl(
            layoutInflater,
            parent
        )

    fun getSeeAll(parent: ViewGroup?) = SeeAllImpl(layoutInflater, parent)
    fun getMovieGroupView(parent: ViewGroup?) =
        MovieGroupItemViewImpl(layoutInflater, parent, this)

}
