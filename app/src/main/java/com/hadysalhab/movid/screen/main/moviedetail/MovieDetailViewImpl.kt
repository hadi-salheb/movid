package com.hadysalhab.movid.screen.main.moviedetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Backdrops
import com.synnapps.carouselview.CarouselView

class MovieDetailViewImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : MovieDetailView() {
    private val carouselView: CarouselView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_movie_detail, parent, false))
        carouselView = findViewById(R.id.carouselView)
    }

    override fun displayCarouselImages(backdrops: List<Backdrops>) {
        Log.d("TAG", "displayCarouselImages: $backdrops")

        carouselView.setImageListener { position, imageView ->
            Glide.with(getContext())
                .load(backdrops[position].filePath)
                .into(imageView)
        }
        carouselView.pageCount = backdrops.size
    }
}