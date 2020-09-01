package com.hadysalhab.movid.common.di.activity

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import com.google.gson.Gson
import com.hadysalhab.movid.account.*
import com.hadysalhab.movid.account.details.FetchAccountDetailsUseCaseSync
import com.hadysalhab.movid.authentication.*
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.ErrorMessageHandler
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.movies.usecases.groups.FetchMovieGroupsUseCase
import com.hadysalhab.movid.movies.usecases.latest.FetchLatestMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCaseFactory
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCaseFactoryImpl
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.reviews.FetchReviewsUseCase
import com.hadysalhab.movid.movies.usecases.reviews.FetchReviewsUseCaseSync
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.persistence.AccountDao
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.AppNavigator
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster
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
    fun getFetchMovieDetailUseCase(
        tmdbApi: TmdbApi,
        moviesStateManager: MoviesStateManager,
        dataValidator: DataValidator, timeProvider: TimeProvider,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler,
        sessionIdUseCaseSync: GetSessionIdUseCaseSync
    ): FetchMovieDetailUseCase =
        FetchMovieDetailUseCase(
            tmdbApi,
            moviesStateManager,
            timeProvider,
            dataValidator,
            schemaToModelHelper,
            errorMessageHandler,
            sessionIdUseCaseSync
        )

    @Provides
    fun toastHelper(activityContext: Context) = ToastHelper(activityContext)

    @Provides
    fun getSchemaToModelHelper() = SchemaToModelHelper()

    @Provides
    fun getErrorMessageHandler(gson: Gson) = ErrorMessageHandler(gson)

    @Provides
    fun getFetchListUseCaseFactory(
        fetchPopularMoviesUseCaseSync: FetchPopularMoviesUseCaseSync,
        fetchTopRatedMoviesUseCaseSync: FetchTopRatedMoviesUseCaseSync,
        fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
        fetchNowPlayingMoviesUseCaseSync: FetchNowPlayingMoviesUseCaseSync,
        fetchSimilarMoviesUseCaseSync: FetchSimilarMoviesUseCaseSync,
        fetchRecommendedMoviesUseCaseSync: FetchRecommendedMoviesUseCaseSync,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        schemaToModelHelper: SchemaToModelHelper,
        timeProvider: TimeProvider,
        errorMessageHandler: ErrorMessageHandler,
        dataValidator: DataValidator,
        moviesStateManager: MoviesStateManager
    ): FetchMovieListUseCaseFactory = FetchMovieListUseCaseFactoryImpl(
        fetchPopularMoviesUseCaseSync,
        fetchTopRatedMoviesUseCaseSync,
        fetchUpcomingMoviesUseCaseSync,
        fetchNowPlayingMoviesUseCaseSync,
        fetchSimilarMoviesUseCaseSync,
        fetchRecommendedMoviesUseCaseSync,
        backgroundThreadPoster,
        uiThreadPoster,
        schemaToModelHelper,
        timeProvider,
        errorMessageHandler,
        dataValidator,
        moviesStateManager
    )

    @Provides
    fun getFetchMovieGroupsUseCase(
        fetchPopularMoviesUseCaseSync: FetchPopularMoviesUseCaseSync,
        fetchTopRatedMoviesUseCaseSync: FetchTopRatedMoviesUseCaseSync,
        fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
        fetchNowPlayingMoviesUseCaseSync: FetchNowPlayingMoviesUseCaseSync,
        fetchLatestMoviesUseCaseSync: FetchLatestMoviesUseCaseSync,
        errorMessageHandler: ErrorMessageHandler,
        schemaToModelHelper: SchemaToModelHelper,
        moviesStateManager: MoviesStateManager,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        dataValidator: DataValidator,
        timeProvider: TimeProvider,
        deviceConfigManager: DeviceConfigManager
    ): FetchMovieGroupsUseCase =
        FetchMovieGroupsUseCase(
            fetchPopularMoviesUseCaseSync,
            fetchTopRatedMoviesUseCaseSync,
            fetchUpcomingMoviesUseCaseSync,
            fetchNowPlayingMoviesUseCaseSync,
            fetchLatestMoviesUseCaseSync,
            dataValidator,
            moviesStateManager,
            timeProvider,
            backgroundThreadPoster,
            uiThreadPoster,
            schemaToModelHelper,
            errorMessageHandler,
            deviceConfigManager
        )

    @Provides
    fun getFetchReviewsUseCase(
        reviewsUseCaseSync: FetchReviewsUseCaseSync,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler,
        dataValidator: DataValidator,
        moviesStateManager: MoviesStateManager
    ) = FetchReviewsUseCase(
        reviewsUseCaseSync,
        backgroundThreadPoster,
        uiThreadPoster,
        schemaToModelHelper,
        errorMessageHandler,
        dataValidator,
        moviesStateManager
    )

    @Provides
    fun getLoginUseCase(
        createRequestTokenUseCaseSync: CreateRequestTokenUseCaseSync,
        signTokenUseCaseSync: SignTokenUseCaseSync,
        createSessionUseCaseSync: CreateSessionUseCaseSync,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        gson: Gson,
        fetchAccountDetailsUseCaseSync: FetchAccountDetailsUseCaseSync,
        schemaToModelHelper: SchemaToModelHelper,
        sharedPreferencesManager: SharedPreferencesManager,
        dao: AccountDao,
        userStateManager: UserStateManager
    ): LoginUseCase =
        LoginUseCase(
            createRequestTokenUseCaseSync,
            signTokenUseCaseSync,
            createSessionUseCaseSync,
            fetchAccountDetailsUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster,
            gson,
            schemaToModelHelper,
            sharedPreferencesManager,
            dao,
            userStateManager
        )

    @Provides
    fun getFetchReviewsUseCaseSync(tmdbApi: TmdbApi) = FetchReviewsUseCaseSync(tmdbApi)

    @Provides
    fun getGetAccountDetailsUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync
    ) = GetAccountDetailsUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        getAccountDetailsUseCaseSync
    )

    @Provides
    fun getGetAccountDetailsUseCaseSync(
        dao: AccountDao,
        userStateManager: UserStateManager,
        dataValidator: DataValidator
    ) = GetAccountDetailsUseCaseSync(dao, userStateManager, dataValidator)

    @Provides
    fun getGetSessionIdUseCase(
        userStateManager: UserStateManager,
        sharedPreferencesManager: SharedPreferencesManager,
        dataValidator: DataValidator
    ) = GetSessionIdUseCaseSync(userStateManager, sharedPreferencesManager, dataValidator)

    @Provides
    fun authManager(
        getSessionIdUseCaseSync: GetSessionIdUseCaseSync
    ) = AuthManager(getSessionIdUseCaseSync)

    @Provides
    fun getFetchPopularMoviesUseCaseSync(tmdbApi: TmdbApi) =
        FetchPopularMoviesUseCaseSync(
            tmdbApi
        )


    @Provides
    fun getFetchTopRatedMoviesUseCaseSync(tmdbApi: TmdbApi) =
        FetchTopRatedMoviesUseCaseSync(
            tmdbApi
        )


    @Provides
    fun getFetchUpcomingMoviesUseCaseSync(tmdbApi: TmdbApi) =
        FetchUpcomingMoviesUseCaseSync(
            tmdbApi
        )


    @Provides
    fun getFetchLatestMoviesUseCaseSync(tmdbApi: TmdbApi) =
        FetchLatestMoviesUseCaseSync(
            tmdbApi
        )


    @Provides
    fun getFetchNowPlayingMoviesUseCaseSync(tmdbApi: TmdbApi) =
        FetchNowPlayingMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchSimilarMoviesUseCaseSync(tmdbApi: TmdbApi) =
        FetchSimilarMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchRecommendedMoviesUseCaseSync(tmdbApi: TmdbApi) =
        FetchRecommendedMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getCreateRequestTokenUseCase(tmdbApi: TmdbApi): CreateRequestTokenUseCaseSync =
        CreateRequestTokenUseCaseSync(tmdbApi)

    @Provides
    fun getSignTokenUseCase(tmdbApi: TmdbApi): SignTokenUseCaseSync =
        SignTokenUseCaseSync(tmdbApi)

    @Provides
    fun getCreateSessionUseCase(tmdbApi: TmdbApi): CreateSessionUseCaseSync =
        CreateSessionUseCaseSync(tmdbApi)

    @Provides
    fun getFetchAccountDetailsUseCaseSync(tmdbApi: TmdbApi): FetchAccountDetailsUseCaseSync =
        FetchAccountDetailsUseCaseSync(tmdbApi)

    @Provides
    fun getIntentHandler(context: Context) = IntentHandler(context)

    @Provides
    fun getAddToFavoritesUseCaseSync(tmdbApi: TmdbApi) = AddToFavoriteUseCaseSync(tmdbApi)

    @Provides
    fun getAddToFavoritesUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        moviesStateManager: MoviesStateManager,
        sessionIdUseCaseSync: GetSessionIdUseCaseSync,
        accountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
        addToFavoriteUseCaseSync: AddToFavoriteUseCaseSync,
        errorMessageHandler: ErrorMessageHandler

    ) = AddToFavoriteUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        moviesStateManager,
        sessionIdUseCaseSync,
        accountDetailsUseCaseSync,
        addToFavoriteUseCaseSync,
        errorMessageHandler
    )
}