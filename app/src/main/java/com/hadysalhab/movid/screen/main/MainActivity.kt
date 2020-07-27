package com.hadysalhab.movid.screen.main

import android.os.Bundle
import android.widget.FrameLayout
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView.Listener,FragmentFrameHost {
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var view: MainView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
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

    }

    override fun getFragmentFrame(): FrameLayout = view.getFragmentFrame()

    override fun onBackPressed() {
        if (mainNavigator.isRootFragment()) {
            super.onBackPressed()
        } else {
            mainNavigator.navigateUp()
        }
    }

}