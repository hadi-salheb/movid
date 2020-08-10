package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class CardGroupView : BaseObservableViewMvc<CardGroupView.Listener>() {
    interface Listener {
        fun onCardClicked(cardID: Int)
        fun onSeeAllClicked(cardGroupType: GroupType)
    }
}