package com.hadysalhab.movid.screen.common.cardgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.movies.Cast
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.cast.CastCard
import com.hadysalhab.movid.screen.common.seeall.SeeAll

class CastsView(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : CardGroupViewImpl<DataGroup<Cast>, CastsView.Listener>(layoutInflater, parent),
    CastCard.Listener, SeeAll.Listener {

    interface Listener {
        fun onCastCardClicked(castID: Int)
        fun onCastSeeAllClicked(groupType: GroupType)
    }


    override fun onCastCardClicked(castID: Int) {
        listeners.forEach {
            it.onCastCardClicked(castID)
        }
    }

    override fun displayCardGroup(data: DataGroup<Cast>, maxNumb: Int?) {
        createCastCardAndAppend(data.data, maxNumb)
    }

    private fun createCastCardAndAppend(casts: List<Cast>, maxNumb: Int?) {
        linearLayout.removeAllViews()
        if (maxNumb == null) {
            casts.forEach { cast ->
                createCastCard(cast)
            }
        } else {
            casts.take(maxNumb).forEach { cast ->
                createCastCard(cast)
            }
            if (casts.size > maxNumb) {
                displaySeeAll()
            }
        }
    }

    private fun createCastCard(cast: Cast) {
        val castCard = viewFactory.getCastCard(linearLayout)
        castCard.registerListener(this)
        castCard.displayCast(cast)
        linearLayout.addView(castCard.getRootView())
    }

    private fun displaySeeAll() {
        val seeAll = viewFactory.getSeeAll(linearLayout)
        seeAll.registerListener(this)
        linearLayout.addView(seeAll.getRootView())
    }

    override fun onSeeAllClicked() {
        listeners.forEach {
            it.onCastSeeAllClicked(this.groupType)
        }
    }

}