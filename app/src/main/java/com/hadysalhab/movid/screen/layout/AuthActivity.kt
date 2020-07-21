package com.hadysalhab.movid.screen.layout

import android.os.Bundle
import android.widget.FrameLayout
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.common.fragmentframe.FragmentFrameView
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import javax.inject.Inject

class AuthActivity : BaseActivity(), FragmentFrameHost {
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var authNavigator: AuthNavigator
    lateinit var view: FragmentFrameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        view = viewFactory.getFragmentFrameView(null)
        setContentView(view.getRootView())
        authNavigator.init(savedInstanceState)
    }

    override fun getFragmentFrame(): FrameLayout = view.getFragmentFrame()
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        authNavigator.onSavedInstanceState(outState)
    }

}