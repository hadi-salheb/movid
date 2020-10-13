package com.hadysalhab.movid.common.di.activity

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import com.google.gson.Gson
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.authentication.AuthManager
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleStateManager
import com.hadysalhab.movid.screen.common.screensnavigator.AppNavigator
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.hadysalhab.movid.screen.main.featuredgroups.FeaturedScreenStateManager
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailScreenStateManager
import com.hadysalhab.movid.screen.main.search.SearchScreenStateManager
import dagger.Module
import dagger.Provides

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
        activityContext: Context
    ): AuthNavigator =
        AuthNavigator(fragmentManager, fragmentFrameHost, activityContext)

    @Provides
    @ActivityScope
    fun getMainNavigator(
        fragmentManager: FragmentManager,
        fragmentFrameHost: FragmentFrameHost,
        activityContext: Context
    ): MainNavigator =
        MainNavigator(fragmentManager, fragmentFrameHost, activityContext)

    @Provides
    @ActivityScope
    fun getAppNavigator(
        activityContext: Context
    ): AppNavigator = AppNavigator(activityContext)

    @Provides
    fun getViewFactory(
        layoutInflater: LayoutInflater
    ): ViewFactory = ViewFactory(layoutInflater)


    @Provides
    fun toastHelper(activityContext: Context) = ToastHelper(activityContext)

    @Provides
    fun getSchemaToModelHelper() = SchemaToModelHelper()

    @Provides
    fun getErrorMessageHandler(gson: Gson) = ErrorMessageHandler(gson)


    @Provides
    fun authManager(
        getSessionIdUseCaseSync: GetSessionIdUseCaseSync
    ) = AuthManager(getSessionIdUseCaseSync)

    @Provides
    fun getIntentHandler(context: Context) = IntentHandler(context)

    @Provides
    fun getFeaturedScreenStateManager() = FeaturedScreenStateManager()

    @Provides
    fun getMovieDetailScreenStateManager() = MovieDetailScreenStateManager()

    @Provides
    fun getListWithToolbarScreenStateManager() = ListWithToolbarTitleStateManager()

    @Provides
    fun getSearchScreenStateManager() = SearchScreenStateManager()
}