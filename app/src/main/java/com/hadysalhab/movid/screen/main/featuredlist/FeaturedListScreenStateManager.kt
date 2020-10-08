package com.hadysalhab.movid.screen.main.featuredlist

import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.Movie


sealed class FeaturedListActions {
    data class SetGroupType(val groupType: String) : FeaturedListActions()
    object FeaturedListRequest : FeaturedListActions()
    data class FeaturedListSuccess(val movies: List<Movie>) : FeaturedListActions()
    data class FeaturedListError(val msg: String) : FeaturedListActions()
    object FeaturedListPaginationError : FeaturedListActions()
    object Pagination : FeaturedListActions()
}

class MovieListScreenStateManager {
    val stateLiveData = MutableLiveData<FeaturedListScreenState>(FeaturedListScreenState())
    private var state: FeaturedListScreenState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(featuredListActions: FeaturedListActions) {
        when (featuredListActions) {
            is FeaturedListActions.SetGroupType -> {
                state = state.copy(
                    groupType = featuredListActions.groupType
                )
            }
            is FeaturedListActions.FeaturedListRequest -> {
                state = state.copy(
                    data = emptyList(),
                    isLoading = true,
                    error = null
                )
            }
            is FeaturedListActions.FeaturedListSuccess -> {
                state = state.copy(
                    isLoading = false,
                    isPaginationLoading = false,
                    error = null,
                    data = featuredListActions.movies
                )
            }
            is FeaturedListActions.Pagination -> {
                state = state.copy(
                    isPaginationLoading = true
                )
            }
            is FeaturedListActions.FeaturedListError -> {
                state = state.copy(
                    data = emptyList(),
                    isLoading = false,
                    error = featuredListActions.msg
                )
            }
            is FeaturedListActions.FeaturedListPaginationError -> {
                state = state.copy(
                    isPaginationLoading = false
                )
            }
        }
    }
}