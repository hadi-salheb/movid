package com.hadysalhab.movid.common.di.application

import com.hadysalhab.movid.common.MyApplication
import com.hadysalhab.movid.common.di.activity.ActivityComponent
import com.hadysalhab.movid.common.di.activity.ActivityModule
import dagger.Component
import javax.inject.Singleton

/**
 * Application-Level component
 */
@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun newActivityComponent(activityModule: ActivityModule): ActivityComponent
    fun inject(myApplication: MyApplication)
}