package com.hadysalhab.movid.screen.common.screensnavigator

import android.content.Context
import android.content.Intent
import com.hadysalhab.movid.screen.main.MainActivity

class AuthNavigator(
    private val context: Context
) {
    fun toMainScreen() {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
}