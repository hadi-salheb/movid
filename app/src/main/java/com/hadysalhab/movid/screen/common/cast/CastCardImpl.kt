package com.hadysalhab.movid.screen.common.cast

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        if (cast.profilePath == null) {
            Glide.with(getContext())
                .load(ContextCompat.getDrawable(getContext(), R.drawable.user_default_profile))
                .into(castIV)
        } else {
            Glide.with(getContext())
                .load(IMAGES_BASE_URL + PROFILE_SIZE_h632 + cast.profilePath)
                .into(castIV)
        }
    }
}