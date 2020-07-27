package com.android.roam.wheelycool.dependencyinjection.presentation

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import dagger.Module
import dagger.Provides

/**
 * Activity-Level module
 */
@Module
class ActivityModule(private val activity: FragmentActivity) {

    @Provides
    fun getActivity(): FragmentActivity = activity

    @Provides
    fun getContext(activity: FragmentActivity): Context = activity

    @Provides
    fun getLayoutInflater(): LayoutInflater = LayoutInflater.from(activity)

    @Provides
    fun getFragmentManager(activity: FragmentActivity): FragmentManager =
        activity.supportFragmentManager

    @Provides
    fun getFragmentFrameHost(activity: FragmentActivity): FragmentFrameHost =
        activity as FragmentFrameHost

    @Provides
    @ActivityScope
    fun getAuthNavigator(
        fragmentManager: FragmentManager,
        fragmentFrameHost: FragmentFrameHost,
        activityContext:Context
    ): AuthNavigator =
        AuthNavigator(fragmentManager, fragmentFrameHost,activityContext)
    @Provides
    @ActivityScope
    fun getMainNavigator(
        fragmentManager: FragmentManager,
        fragmentFrameHost: FragmentFrameHost,
        activityContext:Context
    ): MainNavigator =
        MainNavigator(fragmentManager, fragmentFrameHost,activityContext)

    @Provides
    fun getViewFactory(
        layoutInflater: LayoutInflater
    ): ViewFactory = ViewFactory(layoutInflater)
}