package com.hadysalhab.movid.common.di.activity

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import com.google.gson.Gson
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
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
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
    fun getViewFactory(
        layoutInflater: LayoutInflater
    ): ViewFactory = ViewFactory(layoutInflater)


    @Provides
    fun getFetchMovieDetailUseCase(
        tmdbApi: TmdbApi,
        gson: Gson,
        moviesStateManager: MoviesStateManager,
        dataValidator: DataValidator, timeProvider: TimeProvider
    ): FetchMovieDetailUseCase =
        FetchMovieDetailUseCase(
            tmdbApi,
            gson,
            moviesStateManager,
            timeProvider,
            dataValidator
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
        gson: Gson,
        moviesStateManager: MoviesStateManager,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        dataValidator: DataValidator,
        timeProvider: TimeProvider
    ): FetchMovieGroupsUseCase =
        FetchMovieGroupsUseCase(
            fetchPopularMoviesUseCaseSync,
            fetchTopRatedMoviesUseCaseSync,
            fetchUpcomingMoviesUseCaseSync,
            fetchNowPlayingMoviesUseCaseSync,
            fetchLatestMoviesUseCaseSync,
            dataValidator,
            gson,
            moviesStateManager,
            timeProvider,
            backgroundThreadPoster,
            uiThreadPoster
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
    fun getFetchReviewsUseCaseSync(tmdbApi: TmdbApi) = FetchReviewsUseCaseSync(tmdbApi)


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
}