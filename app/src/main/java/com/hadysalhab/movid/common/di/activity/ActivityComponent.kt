package com.hadysalhab.movid.common.di.activity

import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityModule
import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityScope
import com.hadysalhab.movid.screen.authentication.AuthActivity
import com.hadysalhab.movid.screen.authentication.onboarding.OnBoardingFragment
import com.hadysalhab.movid.screen.main.MainActivity
import com.hadysalhab.movid.screen.main.featured.FeaturedFragment
import com.hadysalhab.movid.screen.splash.SplashActivity
import dagger.Subcomponent

/**
 * Activity-Level component
 */
@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(authActivity: AuthActivity)
    fun inject(onBoardingFragment: OnBoardingFragment)
    fun inject(featuredFragment: FeaturedFragment)
    fun inject(mainActivity: MainActivity)
    fun inject(splashActivity: SplashActivity)
}
