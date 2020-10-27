package com.hadysalhab.movid.screen.common.toasthelper

import android.content.Context
import android.widget.Toast

class ToastHelper(private val context: Context) {
    fun displayMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}