package com.hadysalhab.movid.screen.main

import android.os.Bundle
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity(),MainView.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory
    private lateinit var view: MainView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        view = viewFactory.getMainView(null)
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
        TODO("Not yet implemented")
    }
}