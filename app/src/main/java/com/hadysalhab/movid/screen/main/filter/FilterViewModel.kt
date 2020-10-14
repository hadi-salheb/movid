package com.hadysalhab.movid.screen.main.filter

import androidx.lifecycle.SavedStateHandle
import com.hadysalhab.movid.screen.common.viewmodels.SavedStateViewModel
import javax.inject.Inject

class FilterViewModel @Inject constructor() : SavedStateViewModel() {
    private lateinit var savedStateHandle: SavedStateHandle
    override fun init(savedStateHandle: SavedStateHandle) {
        this.savedStateHandle = savedStateHandle
    }
}