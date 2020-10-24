package com.hadysalhab.movid.screen.main.castlist

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.screen.common.people.People
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class PeopleListViewState(
    val title: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: List<People> = emptyList(),
    @DrawableRes val emptyResultsIconDrawable: Int,
    val emptyResultsMessage: String
)

abstract class PeopleListView : BaseObservableViewMvc<PeopleListView.Listener>() {
    interface Listener {
        fun onBackArrowClicked()
        fun onErrorRetryClicked()
    }

    protected abstract fun hideLoadingIndicator()
    protected abstract fun showLoadingIndicator()
    protected abstract fun showErrorScreen(msg: String)
    protected abstract fun hideErrorScreen()
    protected abstract fun showEmptyDataScreen(@DrawableRes icon: Int, msg: String)
    protected abstract fun hideEmptyDataScreen()
    protected abstract fun showData(data: List<People>)
    abstract fun handleState(peopleListViewState: PeopleListViewState)
}