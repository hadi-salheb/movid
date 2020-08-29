package com.hadysalhab.movid.screen.common.cast

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.common.constants.PROFILE_SIZE_h632
import com.hadysalhab.movid.movies.Cast

class CastCardImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : CastCard() {
    private val castIV: ImageView
    private val castNameTV: TextView
    private val castAsTV: TextView
    private lateinit var cast: Cast

    init {
        setRootView(layoutInflater.inflate(R.layout.component_cast_card, parent, false))
        castIV = findViewById(R.id.iv_cast)
        castNameTV = findViewById(R.id.tv_cast_name)
        castAsTV = findViewById(R.id.tv_cast_as)
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onCastCardClicked(cast.id)
            }
        }
    }

    override fun displayCast(cast: Cast) {
        this.cast = cast
        castNameTV.text = cast.name
        castAsTV.text = cast.character
        cast.profilePath?.let {
            Glide.with(getContext())
                .load(IMAGES_BASE_URL + PROFILE_SIZE_h632 + it)
                .into(castIV)
        }
    }
}