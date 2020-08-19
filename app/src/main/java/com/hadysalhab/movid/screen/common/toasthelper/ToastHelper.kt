package com.hadysalhab.movid.screen.common.toasthelper

import android.content.Context
import android.widget.Toast

class ToastHelper(private val context: Context) {
    fun showFeaturedUpdatedUseCase() {
        Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show()
    }
}