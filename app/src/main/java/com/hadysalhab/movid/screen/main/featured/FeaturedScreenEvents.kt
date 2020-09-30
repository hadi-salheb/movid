package com.hadysalhab.movid.screen.main.featured

sealed class FeaturedScreenEvents

data class ShowUserToastMessage(val toastMessage: String) : FeaturedScreenEvents()