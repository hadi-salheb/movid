package com.hadysalhab.movid.common.di.application

import android.app.Application
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.constants.TMDB_BASE_URL
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.common.firebase.FirebaseCrashlyticsClient
import com.hadysalhab.movid.common.processdeath.ProcessDeathFlagIndicator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.persistence.MovidDB
import com.hadysalhab.movid.persistence.MovidDB.Companion.DATABASE_NAME
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

    @Singleton
    @Provides
    fun getAppDB(app: Application): MovidDB {
        return Room
            .databaseBuilder(app, MovidDB::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun getAccountDao(db: MovidDB) = db.getAccountDao()

    @Provides
    @Singleton
    fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .readTimeout(10, TimeUnit.SECONDS)
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
    fun getGson(): Gson = Gson()


    @Provides
    @Singleton
    fun getSharedPrefManager() =
        SharedPreferencesManager(application)

    @Provides
    @Singleton
    fun getUserStateManager(
        gson: Gson
    ) =
        UserStateManager(gson)

    @Provides
    fun getTimeProvider() = TimeProvider()

    @Singleton
    @Provides
    fun getMoviesStateManager(gson: Gson) =
        MoviesStateManager(gson)

    @Provides
    fun dataValidator(timeProvider: TimeProvider): DataValidator = DataValidator(timeProvider)

    @Provides
    @Singleton
    fun getDiscoverMoviesFilterStateStore() = DiscoverMoviesFilterStateStore()

    @Provides
    @Singleton
    fun getProcessDeathFlagIndicator() = ProcessDeathFlagIndicator()

    @Provides
    fun getFirebaseAnalytics(application: Application) = FirebaseAnalytics.getInstance(application)

    @Provides
    fun getFirebaseCrashlytics() = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun getFirebase(context: Application, firebaseAnalytics: FirebaseAnalytics) =
        FirebaseAnalyticsClient(context, firebaseAnalytics)

    @Provides
    @Singleton
    fun getFirebaseCrashlyticsClient(firebaseCrashlytics: FirebaseCrashlytics) =
        FirebaseCrashlyticsClient(firebaseCrashlytics)
}