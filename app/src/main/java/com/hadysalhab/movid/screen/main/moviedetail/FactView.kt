package com.hadysalhab.movid.screen.main.moviedetail

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class FactView(layoutInflater: LayoutInflater, parent: ViewGroup?) : BaseViewMvc() {
    private val factTV: TextView
    private val factIV: ImageView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_fact, parent, false))
        factIV = findViewById(R.id.fact_logo)
        factTV = findViewById(R.id.fact_text)
    }

    fun displayFact(drawable: Drawable, fact: String) {
        factTV.text = fact
        factIV.setImageDrawable(drawable)
    }
}