package com.hadysalhab.movid.common.di.activity

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.authentication.AuthManager
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.backpress.BackPressDispatcher
import com.hadysalhab.movid.screen.common.dialogs.DialogManager
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleStateManager
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.hadysalhab.movid.screen.main.account.AccountScreenStateManager
import com.hadysalhab.movid.screen.main.castlist.PeopleListScreenStateManager
import com.hadysalhab.movid.screen.main.featuredgroups.FeaturedScreenStateManager
import com.hadysalhab.movid.screen.main.filter.FilterScreenStateManager
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailScreenStateManager
import com.hadysalhab.movid.screen.main.reviews.ReviewScreenStateManager
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
    fun getDialogManager(fragmentManager: FragmentManager) =
        DialogManager(fragmentManager)

    @Provides
    fun getFragmentFrameHost(activity: FragmentActivity): FragmentFrameHost =
        activity as FragmentFrameHost

    @Provides
    @ActivityScope
    fun getAuthNavigator(
        activityContext: Context
    ): AuthNavigator =
        AuthNavigator(activityContext)

    @Provides
    @ActivityScope
    fun getMainNavigator(
        fragmentManager: FragmentManager,
        fragmentFrameHost: FragmentFrameHost,
        activityContext: Context
    ): MainNavigator =
        MainNavigator(fragmentManager, fragmentFrameHost, activityContext)

    @Provides
    fun getViewFactory(
        layoutInflater: LayoutInflater
    ): ViewFactory = ViewFactory(layoutInflater)


    @Provides
    fun toastHelper(activityContext: Context) = ToastHelper(activityContext)

    @Provides
    fun getSchemaToModelHelper() = SchemaToModelHelper()

    @Provides
    fun getErrorMessageHandler(gson: Gson, firebaseAnalyticsClient: FirebaseAnalyticsClient) =
        ErrorMessageHandler(gson, firebaseAnalyticsClient)


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

    @Provides
    fun getReviewScreenStateManager() = ReviewScreenStateManager()

    @Provides
    fun getBackPressDispatcher() = activity as BackPressDispatcher

    @Provides
    fun getFilterScreenStateManager() = FilterScreenStateManager()

    @Provides
    fun getAccountScreenStateManager() = AccountScreenStateManager()

    @Provides
    fun getCastListScreenStateManager() = PeopleListScreenStateManager()
}