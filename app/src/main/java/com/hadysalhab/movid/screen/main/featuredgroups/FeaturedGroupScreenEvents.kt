package com.hadysalhab.movid.screen.main.featuredgroups

sealed class FeaturedScreenEvents

data class ShowUserToastMessage(val toastMessage: String) : FeaturedScreenEvents()