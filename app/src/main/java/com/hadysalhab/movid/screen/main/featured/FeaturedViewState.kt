package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.MoviesResponse

data class FeaturedViewState(
    val isLoading: Boolean = false,
    val isPowerMenuOpen: Boolean = false,
    val errorMessage: String? = null,
    val data: List<MoviesResponse> = emptyList(),
    val powerMenuItem: ToolbarCountryItems
)
