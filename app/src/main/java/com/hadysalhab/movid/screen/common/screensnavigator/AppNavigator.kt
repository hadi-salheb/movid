package com.hadysalhab.movid.screen.common.screensnavigator

import android.content.Context
import android.content.Intent
import com.hadysalhab.movid.screen.authentication.AuthActivity
import com.hadysalhab.movid.screen.main.MainActivity

class AppNavigator(
    private val context: Context
) {
    fun toMainScreen() {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    fun toAuthScreen() {
        val intent = Intent(context, AuthActivity::class.java)
        context.startActivity(intent)
    }

}