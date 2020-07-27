package com.hadysalhab.movid.screen.auth.launcher

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import com.hadysalhab.movid.state.UserStateManager
import com.hadysalhab.movid.usecases.LoginUseCase
import javax.inject.Inject

/**
 * The LauncherFragment serves as the controller for the login screen. {@see [LauncherView] & [LauncherViewImpl]}
 */
class LauncherFragment : BaseFragment(),
    LauncherView.Listener,
    LoginUseCase.Listener {

    private enum class ScreenState {
        IDLE, LOGIN_IN_PROGRESS, LOGIN_ERROR, LOGIN_SUCCESS
    }

    private var screenState = ScreenState.IDLE

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var authNavigator: AuthNavigator

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var userStateManager: UserStateManager

    @Inject
    lateinit var fragActivity: FragmentActivity

    lateinit var view: LauncherView
    private var errorMessage: String = ""


    companion object {
        const val SCREEN_STATE = "SCREEN_STATE"
        private const val TAG = "LauncherFragment"

        @JvmStatic
        fun newInstance() = LauncherFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        if (savedInstanceState != null) {

            screenState = savedInstanceState.getSerializable(SCREEN_STATE) as ScreenState
            Log.d(TAG, "onCreate: $screenState ")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = viewFactory.getLauncherView(container)
        return view.getRootView()
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
                    if (userStateManager.isAuthenticated) {
                        setNewState(ScreenState.LOGIN_SUCCESS)
                    } else {
                        setNewState(ScreenState.IDLE)
                    }
                }
            }
            else -> setNewState(ScreenState.IDLE)
        }
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
        loginUseCase.unregisterListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: $screenState")
        outState.putSerializable(SCREEN_STATE, screenState)
    }


    override fun onLoginClicked(username: String, password: String) {
        loginUseCase.loginAndNotify(username, password)
        setNewState(ScreenState.LOGIN_IN_PROGRESS)
    }

    override fun onSignUpClicked() {
    }

    override fun onLoggedIn(sessionId: String) {
        setNewState(ScreenState.LOGIN_SUCCESS)
    }

    override fun onLoginFailed(msg: String) {
        errorMessage = msg
        setNewState(ScreenState.LOGIN_ERROR)
    }

    private fun setNewState(newState: ScreenState) {
        screenState = newState
        Log.d(TAG, "setNewState: $screenState")
        when (newState) {
            ScreenState.IDLE -> {
                view.showIdleScreen()
            }
            ScreenState.LOGIN_IN_PROGRESS -> {
                view.showProgressState()
            }
            ScreenState.LOGIN_ERROR -> {
                view.hideProgressState()
                Toast.makeText(activityContext, errorMessage, Toast.LENGTH_LONG).show()
            }
            ScreenState.LOGIN_SUCCESS -> {
                view.hideProgressState()
                  authNavigator.toMainScreen()
                  fragActivity.finish()
            }
        }
    }
}