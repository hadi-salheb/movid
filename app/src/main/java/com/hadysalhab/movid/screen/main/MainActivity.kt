package com.hadysalhab.movid.screen.main

import android.os.Bundle
import android.widget.FrameLayout
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.common.controllers.backpress.BackPressDispatcher
import com.hadysalhab.movid.screen.common.controllers.backpress.BackPressListener
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView.Listener, FragmentFrameHost, BackPressDispatcher {

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var view: MainView

    private val backPressedListeners: MutableSet<BackPressListener> = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        view = viewFactory.getMainView(null)
        mainNavigator.init(savedInstanceState)
        setContentView(view.getRootView())
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
    }

    override fun onBottomNavigationItemClicked(item: BottomNavigationItems) {
        val currentSelectedItem = view.getCurrentNavigationItem()
        if (currentSelectedItem == item) {
            mainNavigator.clearCurrentStack()
            return
        }

        if (currentSelectedItem != item && (item == BottomNavigationItems.FEATURED || item == BottomNavigationItems.FAVORITES || item == BottomNavigationItems.WATCHLIST || item == BottomNavigationItems.SEARCH)) {
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

}