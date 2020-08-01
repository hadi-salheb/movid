package com.hadysalhab.movid.common.di.application

import android.app.Application
import com.google.gson.Gson
import com.hadysalhab.movid.authentication.CreateRequestTokenUseCase
import com.hadysalhab.movid.authentication.CreateSessionUseCase
import com.hadysalhab.movid.authentication.LoginUseCase
import com.hadysalhab.movid.authentication.SignTokenUseCase
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.constants.TMDB_BASE_URL
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.user.UserStateManager
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Application-Level module
 */
@Module
class ApplicationModule(private val application: Application) {

    @Provides
    fun getApplication(): Application = application

    @Provides
    fun getDeviceConfigManager(application: Application) = DeviceConfigManager(application)

    @Provides
    @Singleton
    fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun getRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    @Provides
    @Singleton
    fun getTmdbApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)

    @Singleton
    @Provides
    fun getBackgroundThreadPoster(): BackgroundThreadPoster = BackgroundThreadPoster()

    @Singleton
    @Provides
    fun getUiThreadPoster(): UiThreadPoster = UiThreadPoster()

    @Provides
    @Singleton
    fun getCreateRequestTokenUseCase(tmdbApi: TmdbApi): CreateRequestTokenUseCase =
        CreateRequestTokenUseCase(tmdbApi)

    @Provides
    @Singleton
    fun getSignTokenUseCase(tmdbApi: TmdbApi): SignTokenUseCase =
        SignTokenUseCase(tmdbApi)

    @Provides
    @Singleton
    fun getCreateSessionUseCase(tmdbApi: TmdbApi): CreateSessionUseCase =
        CreateSessionUseCase(tmdbApi)

    @Provides
    fun getGson(): Gson = Gson()

    @Provides
    @Singleton
    fun getLoginUseCase(
        createRequestTokenUseCase: CreateRequestTokenUseCase,
        signTokenUseCase: SignTokenUseCase,
        createSessionUseCase: CreateSessionUseCase,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster,
        sharedPreferencesManager: SharedPreferencesManager,
        gson: Gson
    ): LoginUseCase =
        LoginUseCase(
            createRequestTokenUseCase,
            signTokenUseCase,
            createSessionUseCase,
            backgroundThreadPoster,
            uiThreadPoster,
            sharedPreferencesManager,
            gson
        )

    @Provides
    @Singleton
    fun getSharedPrefManager() =
        SharedPreferencesManager(application)

    @Provides
    @Singleton
    fun getUserStateManager(sharedPreferencesManager: SharedPreferencesManager) =
        UserStateManager(sharedPreferencesManager)

    @Singleton
    @Provides
    fun getFetchPopularMoviesUseCase(tmdbApi: TmdbApi) = FetchPopularMoviesUseCase(tmdbApi)

    @Singleton
    @Provides
    fun getFetchTopRatedMoviesUseCase(tmdbApi: TmdbApi) = FetchTopRatedMoviesUseCase(tmdbApi)

    @Singleton
    @Provides
    fun getFetchUpcomingMoviesUseCase(tmdbApi: TmdbApi) = FetchUpcomingMoviesUseCase(tmdbApi)

    @Singleton
    @Provides
    fun getFetchLatestMoviesUseCase(tmdbApi: TmdbApi) = FetchLatestMoviesUseCase(tmdbApi)

    @Singleton
    @Provides
    fun getFetchNowPlayingMoviesUseCase(tmdbApi: TmdbApi) = FetchNowPlayingMoviesUseCase(tmdbApi)

    @Singleton
    @Provides
    fun getMoviesStateManager() = MoviesStateManager()

    @Provides
    @Singleton
    fun getFetchMovieGroupsUseCase(
        fetchPopularMoviesUseCase: FetchPopularMoviesUseCase,
        fetchTopRatedMoviesUseCase: FetchTopRatedMoviesUseCase,
        fetchUpcomingMoviesUseCase: FetchUpcomingMoviesUseCase,
        fetchNowPlayingMoviesUseCase: FetchNowPlayingMoviesUseCase,
        fetchLatestMoviesUseCase: FetchLatestMoviesUseCase,
        gson: Gson,
        moviesStateManager: MoviesStateManager,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster
    ): FetchMovieGroupsUseCase =
        FetchMovieGroupsUseCase(
            fetchPopularMoviesUseCase,
            fetchTopRatedMoviesUseCase,
            fetchUpcomingMoviesUseCase,
            fetchNowPlayingMoviesUseCase,
            fetchLatestMoviesUseCase,
            gson,
            moviesStateManager,
            backgroundThreadPoster,
            uiThreadPoster
        )

    @Provides
    @Singleton
    fun getFetchMovieDetailUseCase(
        tmdbApi: TmdbApi,
        gson: Gson,
        moviesStateManager: MoviesStateManager,
        backgroundThreadPoster: BackgroundThreadPoster,
        uiThreadPoster: UiThreadPoster
    ): FetchMovieDetailUseCase =
        FetchMovieDetailUseCase(
            tmdbApi,
            gson,
            moviesStateManager,
            backgroundThreadPoster,
            uiThreadPoster
        )
}