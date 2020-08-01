package com.hadysalhab.movid.screen.main.moviedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R

class MovieDetailViewImpl (layoutInflater: LayoutInflater,parent:ViewGroup?) : MovieDetailView() {
    init {
        setRootView(layoutInflater.inflate(R.layout.layout_movie_detail,parent,false))
    }
}