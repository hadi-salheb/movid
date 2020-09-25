package com.hadysalhab.movid.screen.main.featured

sealed class FeaturedEvents

data class ShowUserToastMessage(val toastMessage: String) : FeaturedEvents()