package com.hadysalhab.movid.screen.main.search

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class GenreListItem : BaseObservableViewMvc<GenreListItem.Listener>() {
    interface Listener

    abstract fun displayGenre(genre: Genre)
}