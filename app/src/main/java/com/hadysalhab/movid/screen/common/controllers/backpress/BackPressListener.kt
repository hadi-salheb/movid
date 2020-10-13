package com.hadysalhab.movid.screen.common.controllers.backpress

interface BackPressListener {
    /**
     *
     * @return true if the listener handled the back press; false otherwise
     */
    fun onBackPress(): Boolean
}