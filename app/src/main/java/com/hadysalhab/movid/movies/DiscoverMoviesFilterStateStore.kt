package com.hadysalhab.movid.movies

data class FilterState(
    val sortBy: String? = null,
    val includeAdult: Boolean = false,
    val primaryReleaseYearGte: String? = null,
    val primaryReleaseYearLte: String? = null,
    val voteCountGte: Int? = null,
    val voteCountLte: Int? = null,
    val voteAverageGte: Double? = null,
    val voteAverageLte: Double? = null,
    val withRuntimeGte: Int? = null,
    val withRuntimeLte: Int? = null
)

class DiscoverMoviesFilterStateStore {
    var currentFilterState = FilterState()
        get() = FilterState().copy()
        set(value) {
            field = value.copy()
        }

    fun reset() {
        currentFilterState = FilterState()
    }

}
