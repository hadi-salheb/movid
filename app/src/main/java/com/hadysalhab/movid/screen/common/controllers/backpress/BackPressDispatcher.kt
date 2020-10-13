package com.hadysalhab.movid.screen.common.controllers.backpress

interface BackPressDispatcher {
    fun registerListener(backPressListener: BackPressListener)
    fun unregisterListener(backPressListener: BackPressListener)
}