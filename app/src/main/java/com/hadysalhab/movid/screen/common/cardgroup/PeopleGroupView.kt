package com.hadysalhab.movid.screen.common.cardgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.people.People
import com.hadysalhab.movid.screen.common.people.PeopleCard
import com.hadysalhab.movid.screen.common.people.PeopleType
import com.hadysalhab.movid.screen.common.seeall.SeeAll


class PeopleGroupView(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : CardGroupViewImpl<DataGroup<People>, PeopleGroupView.Listener>(layoutInflater, parent),
    PeopleCard.Listener, SeeAll.Listener {
    lateinit var peopleType: PeopleType

    interface Listener {
        fun onPeopleCardClicked(peopleID: Int, peopleType: PeopleType)
        fun onPeopleSeeAllClicked(peopleType: PeopleType)
    }

    override fun displayCardGroup(data: DataGroup<People>, maxNumb: Int?) {
        linearLayout.removeAllViews()
        if (maxNumb == null) {
            data.data.forEach { person ->
                createPeopleCard(person)
            }
        } else {
            data.data.take(maxNumb).forEach { person ->
                createPeopleCard(person)
            }
            if (data.data.size > maxNumb) {
                displaySeeAll()
            }
        }
    }

    private fun createPeopleCard(people: People) {
        this.peopleType = people.peopleType
        val peopleCard = viewFactory.getPeopleCard(linearLayout)
        peopleCard.registerListener(this)
        peopleCard.displayPeople(people)
        linearLayout.addView(peopleCard.getRootView())
    }

    private fun displaySeeAll() {
        val seeAll = viewFactory.getSeeAll(linearLayout)
        seeAll.registerListener(this)
        linearLayout.addView(seeAll.getRootView())
    }

    override fun onSeeAllClicked() {
        listeners.forEach {
            it.onPeopleSeeAllClicked(this.peopleType)
        }
    }

    override fun onPeopleCardClicked(peopleID: Int, peopleType: PeopleType) {
        listeners.forEach {
            it.onPeopleCardClicked(peopleID, peopleType)
        }
    }
}