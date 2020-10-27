package com.hadysalhab.movid.common.di.activity

import com.hadysalhab.movid.common.di.presentation.PresentationComponent
import com.hadysalhab.movid.common.di.presentation.PresentationModule
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class, UseCaseModule::class])
interface ActivityComponent {
    fun newPresentationComponent(presentationModule: PresentationModule): PresentationComponent
}