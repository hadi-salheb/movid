package com.hadysalhab.movid.screen.common.screensnavigator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hadysalhab.movid.screen.auth.launcher.LauncherFragment
import com.hadysalhab.movid.screen.auth.register.RegisterFragment
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.main.MainActivity
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.RootFragmentListener

class AuthNavigator(
    private val fragmentManager: FragmentManager,
    private val fragmentFrameHost: FragmentFrameHost,
    private val context: Context
) {
    lateinit var fragNavController: FragNavController

    private val rootFragmentListener: RootFragmentListener = object : RootFragmentListener {
        override val numberOfRootFragments: Int
            get() = 1

        override fun getRootFragment(index: Int): Fragment {
            return when (index) {
                FragNavController.TAB1 -> LauncherFragment.newInstance()
                else -> throw IllegalStateException("unsupported tab index: $index")
            }
        }
    }

    fun init(savedInstanceState: Bundle?) {
        fragNavController =
            FragNavController(fragmentManager, fragmentFrameHost.getFragmentFrame().id)
        fragNavController.rootFragmentListener = rootFragmentListener
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    fun onSavedInstanceState(savedInstanceState: Bundle?) {
        fragNavController.onSaveInstanceState(savedInstanceState)
    }

    fun navigateUp() {
        fragNavController.popFragment()
    }

    fun toRegisterFragment() = fragNavController.pushFragment(RegisterFragment.newInstance())

    fun isRootFragment(): Boolean = fragNavController.isRootFragment

    fun toMainScreen() {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
}