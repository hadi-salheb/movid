package com.hadysalhab.movid.common.di.activity

import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityModule
import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
}
