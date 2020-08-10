package com.hadysalhab.movid.screen.common.cast

import com.hadysalhab.movid.movies.Cast
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class CastCard : BaseObservableViewMvc<CastCard.Listener>() {
    interface Listener {
        fun onCastCardClicked(castID: Int)
    }

    abstract fun displayCast(cast: Cast)
}