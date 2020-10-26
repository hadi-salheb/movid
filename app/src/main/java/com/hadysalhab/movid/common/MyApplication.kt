package com.hadysalhab.movid.common

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.hadysalhab.movid.common.di.application.ApplicationComponent
import com.hadysalhab.movid.common.di.application.ApplicationModule
import com.hadysalhab.movid.common.di.application.DaggerApplicationComponent
import timber.log.Timber
import javax.inject.Inject

/**
 * Custom application class
 * */
class MyApplication : Application() {
    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().applicationModule(
            ApplicationModule((this))
        ).build()
    }

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val themeModeObserver = Observer<Int> { themeMode ->
        themeMode?.let { AppCompatDelegate.setDefaultNightMode(themeMode) }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appComponent.inject(this)
        sharedPreferencesManager.themeMode.observeForever(themeModeObserver)
    }
}
