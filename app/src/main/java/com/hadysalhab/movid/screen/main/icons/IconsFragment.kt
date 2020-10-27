package com.hadysalhab.movid.screen.main.icons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

class IconsFragment : BaseFragment(), IconList.Listener {

    @Inject
    lateinit var mainNavigator: MainNavigator

    companion object {
        @JvmStatic
        fun newInstance() =
            IconsFragment()
    }

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var intentHandler: IntentHandler

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager


    private lateinit var iconListView: IconList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::iconListView.isInitialized) {
            iconListView = viewFactory.getIconList(container)
        }
        return iconListView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        iconListView.registerListener(this)
        val isDarkMode = sharedPreferencesManager.isDarkMode.value!!
        if (isDarkMode) {
            iconListView.displayIconsWithDarkMode()
        } else {
            iconListView.displayIconsWithLightMode()
        }
    }

    override fun onStop() {
        super.onStop()
        iconListView.unregisterListener(this)
    }

    override fun onIconTagClicked(href: String) {
        intentHandler.handleWebLinkIntent(href)
    }


    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }

}