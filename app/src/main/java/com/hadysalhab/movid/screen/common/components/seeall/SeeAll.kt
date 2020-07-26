package com.hadysalhab.movid.screen.common.components.seeall

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class SeeAll : BaseObservableViewMvc<SeeAll.Listener>() {
    interface Listener {
        fun onSeeAllClicked()
    }
}