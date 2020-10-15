package com.hadysalhab.movid.movies

import com.hadysalhab.movid.screen.main.filter.FilterState


class DiscoverMoviesFilterStateStore {
    var currentFilterState = FilterState()
        get() = field.copy()
        set(value) {
            field = value.copy()
        }

    fun reset() {
        currentFilterState = FilterState()
    }

    fun updateStoreState(savedStoreState: FilterState) {
        currentFilterState = savedStoreState
    }

}
