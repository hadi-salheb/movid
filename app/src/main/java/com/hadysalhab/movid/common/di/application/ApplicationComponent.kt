package com.hadysalhab.movid.common.di.application

import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityModule
import com.hadysalhab.movid.common.di.activity.ActivityComponent
import com.hadysalhab.movid.common.di.viewmodel.ViewModelModule
import dagger.Component
import javax.inject.Singleton

/**
 * Application-Level component
 */
@Singleton
@Component(modules = [ApplicationModule::class,ViewModelModule::class])
interface ApplicationComponent {
    fun newActivityComponent(activityModule: ActivityModule): ActivityComponent
}