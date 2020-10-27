package com.hadysalhab.movid.common.di.presentation

import androidx.savedstate.SavedStateRegistryOwner
import dagger.Module
import dagger.Provides

/**
 * Presentation-Level module
 */
@Module
class PresentationModule(private val savedStateRegistryOwner: SavedStateRegistryOwner) {
    @Provides
    fun savedStateRegistryOwner() = savedStateRegistryOwner

}