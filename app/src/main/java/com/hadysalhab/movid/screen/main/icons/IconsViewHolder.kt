package com.hadysalhab.movid.screen.main.icons

import androidx.recyclerview.widget.RecyclerView


class IconViewHolder(private val iconListItem: IconListItem) :
    RecyclerView.ViewHolder(iconListItem.getRootView()) {
    fun bind(icon: Icon,isDarkMode:Boolean) {
        iconListItem.displayIcon(icon,isDarkMode)
    }
}

