package com.hadysalhab.movid.screen.main.filter

sealed class FilterScreenEvents {
    object PopFragment : FilterScreenEvents()
    data class ShowToast(val msg: String) : FilterScreenEvents()
}