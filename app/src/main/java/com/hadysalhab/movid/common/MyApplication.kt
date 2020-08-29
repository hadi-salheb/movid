package com.hadysalhab.movid.common

import android.app.Application
import com.hadysalhab.movid.common.di.application.ApplicationComponent
import com.hadysalhab.movid.common.di.application.ApplicationModule
import com.hadysalhab.movid.common.di.application.DaggerApplicationComponent

/**
 * Custom application class
 * */
class MyApplication : Application() {
    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().applicationModule(
            ApplicationModule((this))
        ).build()
    }
}
