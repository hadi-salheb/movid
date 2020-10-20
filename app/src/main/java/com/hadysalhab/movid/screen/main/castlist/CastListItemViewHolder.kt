package com.hadysalhab.movid.screen.main.castlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.movies.Cast


sealed class CastListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(cast: Cast) {}
    class CastViewHolder(private val castListItem: CastListItem) :
        CastListItemViewHolder(castListItem.getRootView()) {
        override fun bind(cast: Cast) {
            super.bind(cast)
            castListItem.displayCast(cast)
        }
    }
}