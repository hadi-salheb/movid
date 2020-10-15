package com.hadysalhab.movid.screen.main.filter

import android.os.Parcelable
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterState(
    val sortBy: String = "popularity.desc",
    val includeAdult: Boolean = true,
    val primaryReleaseYearGte: String? = null,
    val primaryReleaseYearLte: String? = null,
    val voteCountGte: Int? = null,
    val voteCountLte: Int? = null,
    val voteAverageGte: Double? = null,
    val voteAverageLte: Double? = null,
    val withRuntimeGte: Int? = null,
    val withRuntimeLte: Int? = null
) : Parcelable

abstract class FilterView : BaseObservableViewMvc<FilterView.Listener>() {
    interface Listener {
        fun onSortByChanged(sortBy: String)
        fun onIncludeAdultChanged(includeAdult: Boolean)
        fun onPrimaryReleaseYearGteChanged(primaryReleaseYearGte: String?)
        fun onPrimaryReleaseYearLteChanged(primaryReleaseYearLte: String?)
        fun onFilterSubmit()
    }

    abstract fun handleState(filterState: FilterState)

}