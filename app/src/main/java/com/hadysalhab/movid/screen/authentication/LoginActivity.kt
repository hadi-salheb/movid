package com.hadysalhab.movid.screen.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.hadysalhab.movid.authentication.AuthManager
import com.hadysalhab.movid.authentication.LoginUseCase
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import javax.inject.Inject

/**
 * The AuthActivity serves as fragments host for the authentication screens. {@see [LauncherFragment] }
 */

const val SCREEN_STATE = "SCREEN_STATE"


class AuthActivity : BaseActivity(), LoginView.Listener,
    LoginUseCase.Listener {
    private enum class ScreenState {
        IDLE, LOGIN_IN_PROGRESS, LOGIN_ERROR, LOGIN_SUCCESS
    }

    private var screenState = ScreenState.IDLE

    @Inject
    lateinit var firebaseAnalyticsClient: FirebaseAnalyticsClient

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var authNavigator: AuthNavigator

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var authManager: AuthManager

    lateinit var view: LoginView

    private var errorMessage: String = ""

    @Inject
    lateinit var intentHandler: IntentHandler

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val nightModeObserver = Observer<Int> { nightMode ->
        nightMode?.let { delegate.localNightMode = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        if (savedInstanceState != null) {
            screenState = savedInstanceState.getSerializable(SCREEN_STATE) as ScreenState
        }
        view = viewFactory.getLoginView(null)
        setContentView(view.getRootView())
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        loginUseCase.registerListener(this)
        when (screenState) {
            // user navigated away before receiving the result
            ScreenState.LOGIN_IN_PROGRESS -> {
                if (loginUseCase.isBusy) {
                    // login flow hasn't completed yet, so just wait for the result
                } else {
                    // login flow completed and we missed the notification
                    if (authManager.isUserAuthenticated()) {
                        setNewState(ScreenState.LOGIN_SUCCESS)
                    } else {
                        setNewState(ScreenState.IDLE)
                    }
                }
            }
            else -> setNewState(ScreenState.IDLE)
        }
        sharedPreferencesManager.nightModeLiveData.observeForever(nightModeObserver)
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
        loginUseCase.unregisterListener(this)
        sharedPreferencesManager.nightModeLiveData.removeObserver(nightModeObserver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SCREEN_STATE, screenState)
    }


    override fun onLoginClicked(username: String, password: String) {
        loginUseCase.loginAndNotify(username, password)
        setNewState(ScreenState.LOGIN_IN_PROGRESS)
    }

    override fun onSignUpClicked() {
        intentHandler.handleSignUpIntent()
    }

    override fun onLoggedIn() {
        setNewState(ScreenState.LOGIN_SUCCESS)
    }

    override fun onLoginFailed(msg: String) {
        errorMessage = msg
        setNewState(ScreenState.LOGIN_ERROR)
    }

    private fun setNewState(newState: ScreenState) {
        screenState = newState
        when (newState) {
            ScreenState.IDLE -> {
                view.showIdleScreen()
            }
            ScreenState.LOGIN_IN_PROGRESS -> {
                view.showProgressState()
            }
            ScreenState.LOGIN_ERROR -> {
                view.hideProgressState()
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
            ScreenState.LOGIN_SUCCESS -> {
                firebaseAnalyticsClient.logLogin()
                view.hideProgressState()
                authNavigator.toMainScreen()
                finish()
            }
        }
    }

}