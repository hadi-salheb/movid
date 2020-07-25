package com.hadysalhab.movid.common.di.activity

import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityModule
import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import com.hadysalhab.movid.screen.auth.AuthActivity
import com.hadysalhab.movid.screen.auth.launcher.LauncherFragment
import com.hadysalhab.movid.screen.splash.SplashActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(authActivity: AuthActivity)
    fun inject(launcherFragment: LauncherFragment)
    fun inject(splashActivity: SplashActivity)
}
