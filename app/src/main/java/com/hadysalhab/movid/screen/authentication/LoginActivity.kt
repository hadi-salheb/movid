package com.hadysalhab.movid.screen.authentication

import android.os.Bundle
import android.widget.Toast
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.authentication.AuthManager
import com.hadysalhab.movid.authentication.LoginUseCase
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.constants.GUEST_SESSION_ID
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.common.dialogs.DialogManager
import com.hadysalhab.movid.screen.common.dialogs.infodialog.InfoDialogEvent
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * The AuthActivity serves as fragments host for the authentication screens. {@see [LauncherFragment] }
 */

const val SCREEN_STATE = "SCREEN_STATE"
const val SIGN_UP_INFO_DIALOG = "SIGN_UP_INFO_DIALOG"


class AuthActivity : BaseActivity(), LoginView.Listener,
    LoginUseCase.Listener {
    private enum class ScreenState {
        IDLE, LOGIN_IN_PROGRESS, LOGIN_ERROR, LOGIN_SUCCESS, DIALOG_INFO
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

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var userStateManager: UserStateManager


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
        EventBus.getDefault().register(this)
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
            ScreenState.DIALOG_INFO -> {
                //do nothing
            }
            else -> setNewState(ScreenState.IDLE)
        }
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
        loginUseCase.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SCREEN_STATE, screenState)
    }


    override fun onLoginClicked(username: String, password: String) {
        setNewState(ScreenState.LOGIN_IN_PROGRESS)
        loginUseCase.loginAndNotify(username, password)
    }

    override fun onSignUpClicked() {
        setNewState(ScreenState.DIALOG_INFO)
    }

    override fun onBrowseClicked() {
        firebaseAnalyticsClient.logBrowsing()
        setNewState(ScreenState.LOGIN_IN_PROGRESS)
        sharedPreferencesManager.setStoredSessionId(GUEST_SESSION_ID)
        userStateManager.updateSessionId(GUEST_SESSION_ID)
        setNewState(ScreenState.LOGIN_SUCCESS)
    }

    override fun onLoggedIn() {
        firebaseAnalyticsClient.logLogin()
        setNewState(ScreenState.LOGIN_SUCCESS)
    }

    override fun onLoginFailed(msg: String) {
        errorMessage = msg
        setNewState(ScreenState.LOGIN_ERROR)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onInfoDialogEvent(event: InfoDialogEvent) {
        when (event) {
            is InfoDialogEvent.Positive -> intentHandler.handleSignUpIntent()
        }
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
                authNavigator.toMainScreen()
                finish()
            }
            ScreenState.DIALOG_INFO -> {
                dialogManager.showSignUpInfoDialog(SIGN_UP_INFO_DIALOG)
            }
        }
    }

}