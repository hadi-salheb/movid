package com.hadysalhab.movid.common.di.activity

import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.account.usecases.details.FetchAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.favmovies.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.authentication.LoginUseCase
import com.hadysalhab.movid.authentication.createsession.CreateSessionUseCaseSync
import com.hadysalhab.movid.authentication.createtoken.CreateRequestTokenUseCaseSync
import com.hadysalhab.movid.authentication.signtoken.SignTokenUseCaseSync
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.usecases.factory.BaseFeaturedMoviesUseCaseFactory
import com.hadysalhab.movid.common.usecases.factory.BaseSimilarRecommendedMoviesUseCaseFactory
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import com.hadysalhab.movid.movies.usecases.list.FetchMoviesResponseUseCase
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.reviews.FetchReviewsUseCase
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.persistence.AccountDao
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster
import dagger.Module
import dagger.Provides

@Module
class UseCaseModel {

    // Factory -------------------------------------------------------------------------------------
    @Provides
    @ActivityScope
    fun getBaseFeaturedMoviesUseCaseFactory(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        moviesStateManager: MoviesStateManager,
        schemaToModelHelper: SchemaToModelHelper,
        timeProvider: TimeProvider,
        dataValidator: DataValidator,
        deviceConfigManager: DeviceConfigManager
    ) = BaseFeaturedMoviesUseCaseFactory(
        deviceConfigManager,
        dataValidator,
        timeProvider,
        moviesStateManager,
        errorMessageHandler,
        schemaToModelHelper,
        tmdbApi
    )

    @Provides
    @ActivityScope
    fun getBaseSimilarRecommendedMoviesUseCaseFactory(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        schemaToModelHelper: SchemaToModelHelper
    ) = BaseSimilarRecommendedMoviesUseCaseFactory(
        errorMessageHandler, schemaToModelHelper, tmdbApi
    )

    //UseCases--------------------------------------------------------------------------------------
    @Provides
    fun getAddToFavoritesUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        moviesStateManager: MoviesStateManager,
        sessionIdUseCaseSync: GetSessionIdUseCaseSync,
        accountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
        errorMessageHandler: ErrorMessageHandler,
        tmdbApi: TmdbApi

    ) = AddRemoveFavMovieUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        moviesStateManager,
        sessionIdUseCaseSync,
        accountDetailsUseCaseSync,
        errorMessageHandler,
        tmdbApi
    )

    @Provides
    fun getFeaturedMoviesUseCase(
        baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster
    ): FetchFeaturedMoviesUseCase =
        FetchFeaturedMoviesUseCase(
            baseFeaturedMoviesUseCaseFactory,
            backgroundThreadPoster,
            uiThreadPoster
        )

    @Provides
    fun getFetchMoviesResponseUseCase(
        baseSimilarRecommendedMoviesUseCaseFactory: BaseSimilarRecommendedMoviesUseCaseFactory,
        baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster
    ) = FetchMoviesResponseUseCase(
        baseSimilarRecommendedMoviesUseCaseFactory,
        baseFeaturedMoviesUseCaseFactory,
        backgroundThreadPoster,
        uiThreadPoster
    )

    @Provides
    fun getFetchPopularMoviesUseCaseSync(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        moviesStateManager: MoviesStateManager,
        schemaToModelHelper: SchemaToModelHelper,
        timeProvider: TimeProvider,
        dataValidator: DataValidator,
        deviceConfigManager: DeviceConfigManager
    ) =
        FetchPopularMoviesUseCaseSync(
            tmdbApi,
            errorMessageHandler,
            moviesStateManager,
            schemaToModelHelper,
            timeProvider,
            dataValidator,
            deviceConfigManager
        )

    @Provides
    fun getFetchTopRatedMoviesUseCaseSync(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        moviesStateManager: MoviesStateManager,
        schemaToModelHelper: SchemaToModelHelper,
        timeProvider: TimeProvider,
        dataValidator: DataValidator,
        deviceConfigManager: DeviceConfigManager
    ) =
        FetchTopRatedMoviesUseCaseSync(
            tmdbApi,
            errorMessageHandler,
            moviesStateManager,
            schemaToModelHelper,
            timeProvider,
            dataValidator,
            deviceConfigManager
        )

    @Provides
    fun getFetchUpcomingMoviesUseCaseSync(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        moviesStateManager: MoviesStateManager,
        schemaToModelHelper: SchemaToModelHelper,
        timeProvider: TimeProvider,
        dataValidator: DataValidator,
        deviceConfigManager: DeviceConfigManager
    ) =
        FetchUpcomingMoviesUseCaseSync(
            tmdbApi,
            errorMessageHandler,
            moviesStateManager,
            schemaToModelHelper,
            timeProvider,
            dataValidator,
            deviceConfigManager
        )

    @Provides
    fun getFetchNowPlayingMoviesUseCaseSync(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        moviesStateManager: MoviesStateManager,
        schemaToModelHelper: SchemaToModelHelper,
        timeProvider: TimeProvider,
        dataValidator: DataValidator,
        deviceConfigManager: DeviceConfigManager
    ) =
        FetchNowPlayingMoviesUseCaseSync(
            tmdbApi,
            errorMessageHandler,
            moviesStateManager,
            schemaToModelHelper,
            timeProvider,
            dataValidator,
            deviceConfigManager
        )

    @Provides
    fun getFetchSimilarMoviesUseCaseSync(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        schemaToModelHelper: SchemaToModelHelper
    ) =
        FetchSimilarMoviesUseCaseSync(
            tmdbApi,
            errorMessageHandler,
            schemaToModelHelper
        )

    @Provides
    fun getFetchRecommendedMoviesUseCaseSync(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler,
        schemaToModelHelper: SchemaToModelHelper
    ) =
        FetchRecommendedMoviesUseCaseSync(
            tmdbApi,
            errorMessageHandler,
            schemaToModelHelper
        )

    @Provides
    fun getCreateRequestTokenUseCase(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler
    ): CreateRequestTokenUseCaseSync =
        CreateRequestTokenUseCaseSync(tmdbApi, errorMessageHandler)

    @Provides
    fun getSignTokenUseCase(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler
    ): SignTokenUseCaseSync =
        SignTokenUseCaseSync(tmdbApi, errorMessageHandler)

    @Provides
    fun getCreateSessionUseCase(
        tmdbApi: TmdbApi,
        errorMessageHandler: ErrorMessageHandler
    ): CreateSessionUseCaseSync =
        CreateSessionUseCaseSync(tmdbApi, errorMessageHandler)

    @Provides
    fun getFetchAccountDetailsUseCaseSync(
        tmdbApi: TmdbApi,
        sharedPreferencesManager: SharedPreferencesManager,
        accountDao: AccountDao,
        errorMessageHandler: ErrorMessageHandler,
        userStateManager: UserStateManager,
        schemaToModelHelper: SchemaToModelHelper
    ): FetchAccountDetailsUseCaseSync =
        FetchAccountDetailsUseCaseSync(
            tmdbApi,
            errorMessageHandler,
            sharedPreferencesManager,
            accountDao,
            userStateManager,
            schemaToModelHelper
        )

    @Provides
    fun getFetchReviewsUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler,
        dataValidator: DataValidator,
        moviesStateManager: MoviesStateManager,
        tmdbApi: TmdbApi
    ) = FetchReviewsUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        schemaToModelHelper,
        errorMessageHandler,
        dataValidator,
        moviesStateManager,
        tmdbApi
    )

    @Provides
    fun getLoginUseCase(
        createRequestTokenUseCaseSync: CreateRequestTokenUseCaseSync,
        signTokenUseCaseSync: SignTokenUseCaseSync,
        createSessionUseCaseSync: CreateSessionUseCaseSync,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        fetchAccountDetailsUseCaseSync: FetchAccountDetailsUseCaseSync
    ): LoginUseCase =
        LoginUseCase(
            createRequestTokenUseCaseSync,
            signTokenUseCaseSync,
            createSessionUseCaseSync,
            fetchAccountDetailsUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster
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


}