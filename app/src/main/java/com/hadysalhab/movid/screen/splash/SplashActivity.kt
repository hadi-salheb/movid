package com.hadysalhab.movid.screen.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.hadysalhab.movid.screen.MainActivity
import com.hadysalhab.movid.screen.auth.AuthActivity
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.state.UserStateManager
import javax.inject.Inject


class SplashActivity : BaseActivity() {
    @Inject
    lateinit var userStateManager: UserStateManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            val intent:Intent = if (userStateManager.isAuthenticated){
                Intent(this,AuthActivity::class.java)
            }else{
                Intent(this,AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        },500)

    }

}