package com.hadysalhab.movid.screen.main.libraries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

class LibrariesFragment : BaseFragment(), LibrariesView.Listener {

    @Inject
    lateinit var mainNavigator: MainNavigator

    companion object {
        @JvmStatic
        fun newInstance() =
            LibrariesFragment()
    }

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var intentHandler: IntentHandler

    private lateinit var librariesView: LibrariesView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::librariesView.isInitialized) {
            librariesView = viewFactory.getLibrariesView(container)
        }
        return librariesView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        librariesView.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        librariesView.unregisterListener(this)
    }

    override fun onLibraryListItemClicked(library: Library) {
        intentHandler.handleLibraryIntent(library.libraryUrl)
    }

    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }

}