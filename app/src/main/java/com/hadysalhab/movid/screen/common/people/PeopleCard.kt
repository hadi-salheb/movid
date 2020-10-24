package com.hadysalhab.movid.screen.common.people

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class PeopleCard : BaseObservableViewMvc<PeopleCard.Listener>() {
    interface Listener {
        fun onPeopleCardClicked(peopleID: Int, peopleType: PeopleType)
    }

    abstract fun displayPeople(people: People)
}