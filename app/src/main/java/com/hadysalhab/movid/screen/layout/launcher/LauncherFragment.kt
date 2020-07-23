package com.hadysalhab.movid.screen.layout.launcher

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.AuthNavigator
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LauncherFragment : BaseFragment(), LauncherView.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var authNavigator: AuthNavigator
    lateinit var view: LauncherView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
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
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LauncherFragment()
    }

    override fun onLoginClicked() {
        Toast.makeText(activityContext, "Login Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onSignUpClicked() {
        authNavigator.toRegisterFragment()
    }
}