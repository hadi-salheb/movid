package com.hadysalhab.movid.screen.main.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import javax.inject.Inject

class AboutFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            AboutFragment()
    }

    @Inject
    lateinit var viewFactory: ViewFactory

    lateinit var aboutView: AboutView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::aboutView.isInitialized) {
            aboutView = viewFactory.getAboutView(container)
        }

        return aboutView.getRootView()
    }
}