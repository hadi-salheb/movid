package com.hadysalhab.movid.screen.main

import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.common.processdeath.ProcessDeathFlagIndicator
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.common.controllers.backpress.BackPressDispatcher
import com.hadysalhab.movid.screen.common.controllers.backpress.BackPressListener
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.ncapdevi.fragnav.FragNavController
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView.Listener, FragmentFrameHost, BackPressDispatcher,
    FragNavController.TransactionListener {

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var firebaseAnalyticsClient: FirebaseAnalyticsClient

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var processDeathFlagIndicator: ProcessDeathFlagIndicator

    private lateinit var view: MainView

    private val backPressedListeners: MutableSet<BackPressListener> = HashSet()

    private val sessionIdObserver = Observer<String> { sessionID ->
        if (sessionID.isNullOrEmpty()) {
            mainNavigator.toAuthActivity()
            this.finish()
        }
    }

    private val nightModeObserver = Observer<Int> { nightMode ->
        nightMode?.let { delegate.localNightMode = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        if (processDeathFlagIndicator.isKilled) {
            firebaseAnalyticsClient.logMainActivityProcessDeath()
            mainNavigator.toSplashActivity()
            finish()
        }
        setTheme(R.style.Theme_MyApp)
        super.onCreate(savedInstanceState)
        view = viewFactory.getMainView(null)
        mainNavigator.init(savedInstanceState, this)
        setContentView(view.getRootView())
        sharedPreferencesManager.nightModeLiveData.observe(this, nightModeObserver)
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        sharedPreferencesManager.sessionId.observeForever(sessionIdObserver)
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
        sharedPreferencesManager.sessionId.removeObserver(sessionIdObserver)
    }

    override fun onBottomNavigationItemClicked(item: BottomNavigationItems) {
        val currentSelectedItem = view.getCurrentNavigationItem()
        if (currentSelectedItem == item) {
            mainNavigator.clearCurrentStack()
        } else {
            mainNavigator.switchTab(item)
        }
    }

    override fun getFragmentFrame(): FrameLayout = view.getFragmentFrame()
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mainNavigator.onSavedInstanceState(outState)
    }

    override fun onBackPressed() {
        var isBackPressConsumedByAnyListener = false
        for (backPressedListener in backPressedListeners) {
            val isConsumed = backPressedListener.onBackPress()
            if (isConsumed) {
                isBackPressConsumedByAnyListener = true
                break
            }
        }
        if (isBackPressConsumedByAnyListener) {
            return
        } else {
            if (mainNavigator.isRootFragment()) {
                super.onBackPressed()
            } else {
                mainNavigator.navigateUp()
            }
        }
    }

    override fun registerListener(backPressListener: BackPressListener) {
        backPressedListeners.add(backPressListener)
    }

    override fun unregisterListener(backPressListener: BackPressListener) {
        backPressedListeners.remove(backPressListener)
    }

    override fun onFragmentTransaction(
        fragment: Fragment?,
        transactionType: FragNavController.TransactionType
    ) {
        fragment?.let {
            fragment::class.simpleName?.let {
                firebaseAnalyticsClient.logFragmentTransaction(it)
            }
        }
    }

    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        firebaseAnalyticsClient.logTabTransaction(BottomNavigationItems.values()[index])
    }

}