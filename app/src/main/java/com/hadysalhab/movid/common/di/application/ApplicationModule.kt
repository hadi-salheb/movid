package com.hadysalhab.movid.common.di.application

import android.app.Application
import com.google.gson.Gson
import com.hadysalhab.movid.common.constants.TMDB_BASE_URL
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.persistence.SharedPreferencesManager
import com.hadysalhab.movid.state.UserStateManager
import com.hadysalhab.movid.usecases.CreateRequestTokenUseCase
import com.hadysalhab.movid.usecases.CreateSessionUseCase
import com.hadysalhab.movid.usecases.LoginUseCase
import com.hadysalhab.movid.usecases.SignTokenUseCase
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
    fun getSharedPrefManager() = SharedPreferencesManager(application)

    @Provides
    @Singleton
    fun getUserStateManager(sharedPreferencesManager: SharedPreferencesManager) =
        UserStateManager(sharedPreferencesManager)
}