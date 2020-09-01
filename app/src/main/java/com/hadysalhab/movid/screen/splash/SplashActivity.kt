package com.hadysalhab.movid.screen.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.hadysalhab.movid.authentication.AuthManager
import com.hadysalhab.movid.screen.authentication.AuthActivity
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.main.MainActivity
import javax.inject.Inject

/**
 * The SplashActivity serves as a splash screen and navigates to the appropriate screen based on the user authentication state }
 */

class SplashActivity : BaseActivity() {
    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            val intent: Intent =
                if (authManager.isUserAuthenticated()) {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, AuthActivity::class.java)
                }
            startActivity(intent)
            finish()
        }, 500)

    }

}