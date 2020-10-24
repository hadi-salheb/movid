package com.hadysalhab.movid.movies

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class FilterStoreState(
    val sortBy: String = "popularity.desc",
    val includeAdult: Boolean = false,
    val primaryReleaseYearGte: String? = null,
    val primaryReleaseYearLte: String? = null,
    val voteCountGte: Int? = null,
    val voteCountLte: Int? = null,
    val voteAverageGte: Float? = null,
    val voteAverageLte: Float? = null,
    val withRuntimeGte: Int? = null,
    val withRuntimeLte: Int? = null
) : Parcelable

class DiscoverMoviesFilterStateStore {
    var currentFilterState = FilterStoreState()
        get() = field.copy()
        set(value) {
            field = value.copy()
        }

    fun reset() {
        currentFilterState = FilterStoreState()
    }

    fun updateStoreState(savedStoreViewState: FilterStoreState) {
        currentFilterState = savedStoreViewState
    }

}
