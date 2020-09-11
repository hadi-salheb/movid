package com.hadysalhab.movid.screen.main.featured

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.R

enum class ToolbarCountryItems(
    val countryName: String,
    val iso: String,
    @DrawableRes val countryIcon: Int
) {
    AUSTRALIA("Australia", "AU", R.drawable.ic_australia),
    USA("USA", "US", R.drawable.ic_united_states),
    FRANCE("France", "FR", R.drawable.ic_france),
    INDIA("India", "IN", R.drawable.ic_india),
    UNITED_KINGDOM("UK", "GB", R.drawable.ic_united_kingdom)
}