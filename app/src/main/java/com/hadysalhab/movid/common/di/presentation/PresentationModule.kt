package com.android.roam.wheelycool.dependencyinjection.presentation

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