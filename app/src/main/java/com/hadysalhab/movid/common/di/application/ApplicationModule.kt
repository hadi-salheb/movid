package com.hadysalhab.movid.common.di.application

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    fun getApplication(): Application = application


}