package com.hadysalhab.movid.screen.common.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

abstract class SavedStateViewModel : ViewModel() {
    abstract fun init(savedStateHandle: SavedStateHandle)
}