package com.hadysalhab.movid.common.di.activity

import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.account.usecases.details.FetchAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCase
import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.details.UpdateAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.favorite.AddRemoveFavMovieUseCase
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.account.usecases.watchlist.AddRemoveWatchlistMovieUseCase
import com.hadysalhab.movid.authentication.LoginUseCase
import com.hadysalhab.movid.authentication.SignOutUseCase
import com.hadysalhab.movid.authentication.createsession.CreateSessionUseCaseSync
import com.hadysalhab.movid.authentication.createtoken.CreateRequestTokenUseCaseSync
import com.hadysalhab.movid.authentication.signtoken.SignTokenUseCaseSync
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.usecases.factory.BaseFeaturedMoviesUseCaseFactory
import com.hadysalhab.movid.common.usecases.factory.BaseSimilarRecommendedMoviesUseCaseFactory
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.credits.FetchCreditsUseCase
import com.hadysalhab.movid.movies.usecases.detail.FetchMovieDetailUseCase
import com.hadysalhab.movid.movies.usecases.discover.DiscoverMoviesUseCase
import com.hadysalhab.movid.movies.usecases.favorites.FetchFavoriteMoviesUseCase
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import com.hadysalhab.movid.movies.usecases.list.FetchFeaturedListUseCase
import com.hadysalhab.movid.movies.usecases.list.FetchRecommendedSimilarListUseCase
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.reviews.FetchReviewsUseCase
import com.hadysalhab.movid.movies.usecases.search.SearchMovieUseCase
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.watchlist.FetchWatchlistMoviesUseCase
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.persistence.AccountDao
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    // Factory -------------------------------------------------------------------------------------
    @Provides
    @ActivityScope
    fun getBaseFeaturedMoviesUseCaseFactory(
        tmdbApi: TmdbApi
    ) = BaseFeaturedMoviesUseCaseFactory(
        tmdbApi
    )

    @Provides
    @ActivityScope
    fun getBaseSimilarRecommendedMoviesUseCaseFactory(
        tmdbApi: TmdbApi
    ) = BaseSimilarRecommendedMoviesUseCaseFactory(
        tmdbApi
    )

    //UseCases--------------------------------------------------------------------------------------
    @Provides
    fun getAddToFavoritesUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        sessionIdUseCaseSync: GetSessionIdUseCaseSync,
        accountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
        errorMessageHandler: ErrorMessageHandler,
        tmdbApi: TmdbApi,
        moviesStateManager: MoviesStateManager
    ) = AddRemoveFavMovieUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        sessionIdUseCaseSync,
        accountDetailsUseCaseSync,
        errorMessageHandler,
        tmdbApi,
        moviesStateManager
    )

    @Provides
    fun getAddToWatchlistUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        sessionIdUseCaseSync: GetSessionIdUseCaseSync,
        accountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
        errorMessageHandler: ErrorMessageHandler,
        tmdbApi: TmdbApi,
        moviesStateManager: MoviesStateManager
    ) = AddRemoveWatchlistMovieUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        sessionIdUseCaseSync,
        accountDetailsUseCaseSync,
        errorMessageHandler,
        tmdbApi,
        moviesStateManager
    )

    @Provides
    fun getFeaturedMoviesUseCase(
        baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        errorMessageHandler: ErrorMessageHandler,
        schemaToModelHelper: SchemaToModelHelper,
        timeProvider: TimeProvider,
        moviesStateManager: MoviesStateManager
    ): FetchFeaturedMoviesUseCase =
        FetchFeaturedMoviesUseCase(
            baseFeaturedMoviesUseCaseFactory,
            timeProvider,
            backgroundThreadPoster,
            errorMessageHandler,
            schemaToModelHelper,
            uiThreadPoster,
            moviesStateManager
        )

    @Provides
    fun getFetchMoviesResponseUseCase(
        baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        errorMessageHandler: ErrorMessageHandler,
        schemaToModelHelper: SchemaToModelHelper,
        moviesStateManager: MoviesStateManager,
        timeProvider: TimeProvider
    ) = FetchFeaturedListUseCase(
        baseFeaturedMoviesUseCaseFactory,
        backgroundThreadPoster,
        uiThreadPoster,
        schemaToModelHelper,
        errorMessageHandler,
        moviesStateManager,
        timeProvider
    )

    @Provides
    fun getFetchPopularMoviesUseCaseSync(
        tmdbApi: TmdbApi
    ) =
        FetchPopularMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchTopRatedMoviesUseCaseSync(
        tmdbApi: TmdbApi
    ) =
        FetchTopRatedMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchUpcomingMoviesUseCaseSync(
        tmdbApi: TmdbApi
    ) =
        FetchUpcomingMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchNowPlayingMoviesUseCaseSync(
        tmdbApi: TmdbApi
    ) =
        FetchNowPlayingMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchSimilarMoviesUseCaseSync(
        tmdbApi: TmdbApi
    ) =
        FetchSimilarMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchRecommendedMoviesUseCaseSync(
        tmdbApi: TmdbApi
    ) =
        FetchRecommendedMoviesUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getCreateRequestTokenUseCase(
        tmdbApi: TmdbApi
    ): CreateRequestTokenUseCaseSync =
        CreateRequestTokenUseCaseSync(tmdbApi)

    @Provides
    fun getSignTokenUseCase(
        tmdbApi: TmdbApi
    ): SignTokenUseCaseSync =
        SignTokenUseCaseSync(tmdbApi)

    @Provides
    fun getCreateSessionUseCase(
        tmdbApi: TmdbApi
    ): CreateSessionUseCaseSync =
        CreateSessionUseCaseSync(tmdbApi)

    @Provides
    fun getFetchAccountDetailsUseCaseSync(
        tmdbApi: TmdbApi
    ): FetchAccountDetailsUseCaseSync =
        FetchAccountDetailsUseCaseSync(
            tmdbApi
        )

    @Provides
    fun getFetchReviewsUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler,
        tmdbApi: TmdbApi
    ) = FetchReviewsUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        schemaToModelHelper,
        errorMessageHandler,
        tmdbApi
    )

    @Provides
    fun getLoginUseCase(
        createRequestTokenUseCaseSync: CreateRequestTokenUseCaseSync,
        signTokenUseCaseSync: SignTokenUseCaseSync,
        createSessionUseCaseSync: CreateSessionUseCaseSync,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        fetchAccountDetailsUseCaseSync: FetchAccountDetailsUseCaseSync,
        errorMessageHandler: ErrorMessageHandler,
        updateAccountDetailsUseCaseSync: UpdateAccountDetailsUseCaseSync,
        schemaToModelHelper: SchemaToModelHelper
    ): LoginUseCase =
        LoginUseCase(
            createRequestTokenUseCaseSync,
            signTokenUseCaseSync,
            createSessionUseCaseSync,
            fetchAccountDetailsUseCaseSync,
            updateAccountDetailsUseCaseSync,
            schemaToModelHelper,
            backgroundThreadPoster, uiThreadPoster, errorMessageHandler
        )

    @Provides
    fun getUpdatedUserStateUseCaseSync(
        sharedPreferencesManager: SharedPreferencesManager,
        userStateManager: UserStateManager,
        accountDao: AccountDao
    ) = UpdateAccountDetailsUseCaseSync(
        sharedPreferencesManager, accountDao, userStateManager
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
        timeProvider: TimeProvider,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler,
        sessionIdUseCaseSync: GetSessionIdUseCaseSync,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster
    ): FetchMovieDetailUseCase =
        FetchMovieDetailUseCase(
            tmdbApi,
            schemaToModelHelper,
            errorMessageHandler,
            sessionIdUseCaseSync,
            timeProvider,
            moviesStateManager,
            backgroundThreadPoster,
            uiThreadPoster
        )

    @Provides
    fun getFavoriteMoviesUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler,
        tmdbApi: TmdbApi,
        getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
        getSessionIdUseCaseSync: GetSessionIdUseCaseSync
    ): FetchFavoriteMoviesUseCase = FetchFavoriteMoviesUseCase(
        getSessionIdUseCaseSync,
        getAccountDetailsUseCaseSync,
        backgroundThreadPoster,
        errorMessageHandler,
        schemaToModelHelper,
        uiThreadPoster,
        tmdbApi
    )

    @Provides
    fun getWatchlistMoviesUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler,
        tmdbApi: TmdbApi,
        getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
        getSessionIdUseCaseSync: GetSessionIdUseCaseSync
    ): FetchWatchlistMoviesUseCase = FetchWatchlistMoviesUseCase(
        getSessionIdUseCaseSync,
        getAccountDetailsUseCaseSync,
        backgroundThreadPoster,
        errorMessageHandler,
        schemaToModelHelper,
        uiThreadPoster,
        tmdbApi
    )

    @Provides
    fun getSearchMovieUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        tmdbApi: TmdbApi,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler

    ) = SearchMovieUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        tmdbApi,
        schemaToModelHelper,
        errorMessageHandler
    )

    @Provides
    fun getDiscoverMoviesUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        errorMessageHandler: ErrorMessageHandler,
        schemaToModelHelper: SchemaToModelHelper,
        uiThreadPoster: UiThreadPoster,
        discoverMoviesFilterStateStore: DiscoverMoviesFilterStateStore,
        tmdbApi: TmdbApi
    ) = DiscoverMoviesUseCase(
        backgroundThreadPoster,
        errorMessageHandler,
        schemaToModelHelper,
        uiThreadPoster,
        discoverMoviesFilterStateStore, tmdbApi
    )

    @Provides
    fun getRecommendedSimilarMoviesUseCase(
        baseSimilarRecommendedMoviesUseCaseFactory: BaseSimilarRecommendedMoviesUseCaseFactory,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler
    ) = FetchRecommendedSimilarListUseCase(
        baseSimilarRecommendedMoviesUseCaseFactory,
        backgroundThreadPoster,
        uiThreadPoster,
        schemaToModelHelper,
        errorMessageHandler
    )

    @Provides
    fun getAccountDetailUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        getGetAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync
    ) = GetAccountDetailsUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        getGetAccountDetailsUseCaseSync
    )

    @Provides
    fun getSignOutUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        sharedPreferencesManager: SharedPreferencesManager,
        dao: AccountDao,
        userStateManager: UserStateManager,
        moviesStateManager: MoviesStateManager,
        filterStateStore: DiscoverMoviesFilterStateStore
    ) = SignOutUseCase(
        backgroundThreadPoster,
        sharedPreferencesManager,
        dao,
        moviesStateManager,
        userStateManager,
        filterStateStore
    )

    @Provides
    fun getFetchCreditsUseCase(
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        tmdbApi: TmdbApi,
        schemaToModelHelper: SchemaToModelHelper,
        errorMessageHandler: ErrorMessageHandler
    ) = FetchCreditsUseCase(
        backgroundThreadPoster,
        uiThreadPoster,
        tmdbApi,
        schemaToModelHelper,
        errorMessageHandler
    )


}