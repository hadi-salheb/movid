package com.hadysalhab.movid.screen.main.moviedetail

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class FactView(layoutInflater: LayoutInflater, parent: ViewGroup?) : BaseViewMvc() {
    private val factTV: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_fact, parent, false))
        factTV = findViewById(R.id.fact_text)
    }

    fun displayFact(drawable: Drawable, fact: String) {
        factTV.text = fact
        factTV.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}