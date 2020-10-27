package com.hadysalhab.movid.screen.main.privacypolicy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

class PrivacyPolicyFragment : BaseFragment(), PrivacyPolicyView.Listener {

    @Inject
    lateinit var mainNavigator: MainNavigator

    companion object {
        @JvmStatic
        fun newInstance() =
            PrivacyPolicyFragment()
    }

    @Inject
    lateinit var intentHandler: IntentHandler

    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var privatePolicyView: PrivacyPolicyView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::privatePolicyView.isInitialized) {
            privatePolicyView = viewFactory.getPrivacyPolicyView(container)
        }

        return privatePolicyView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        privatePolicyView.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        privatePolicyView.unregisterListener(this)
    }

    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }

    override fun onTagClicked(href: String?) {
        intentHandler.handlePrivacyPolicyTags(href)
    }
}