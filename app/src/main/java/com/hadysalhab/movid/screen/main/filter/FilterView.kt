package com.hadysalhab.movid.screen.main.filter

import android.os.Parcelable
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterViewState(
    val sortByOption: SortOption = SortOption.POPULARITY,
    val sortByOrder: SortOrder = SortOrder.DESC,
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

enum class SortOption(val sortOptionValue: String) {
    POPULARITY("Popularity"),
    RELEASE_DATE("Release Date"),
    ORIGINAL_TITLE("Original Title"),
    VOTE_AVERAGE("Vote Average"),
    VOTE_COUNT("Vote Count")
}

enum class SortOrder(val sortOrderValue: String) {
    ASC("Asc"),
    DESC("Desc")
}

abstract class FilterView : BaseObservableViewMvc<FilterView.Listener>() {
    interface Listener {
        fun onSortByOptionChanged(sortByOption: SortOption)
        fun onSortByOrderChanged(sort: SortOrder)
        fun onIncludeAdultChanged(includeAdult: Boolean)
        fun onPrimaryReleaseYearGteChanged(primaryReleaseYearGte: String?)
        fun onPrimaryReleaseYearLteChanged(primaryReleaseYearLte: String?)
        fun onFilterSubmit()
        fun onVoteAverageGteChanged(voteAverageGte: Float?)
        fun onVoteAverageLteChanged(voteAverageLte: Float?)
        fun onVoteCountGteChanged(voteCountGte: Int?)
        fun onVoteCountLteChanged(voteCountLte: Int?)
        fun onRuntimeGteChanged(withRuntimeGte: Int?)
        fun onRuntimeLteChanged(withRuntimeLte: Int?)
        fun onResetClick()
        fun onBackArrowClicked()
    }

    abstract fun handleState(filterViewState: FilterViewState)

}