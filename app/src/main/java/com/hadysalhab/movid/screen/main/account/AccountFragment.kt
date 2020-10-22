package com.hadysalhab.movid.screen.main.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

class AccountFragment : BaseFragment(), AccountView.Listener {

    companion object {
        @JvmStatic
        fun newInstance() =
            AccountFragment()
    }

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    private lateinit var accountViewModel: AccountViewModel

    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var accountView: AccountView

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var intentHandler: IntentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        accountViewModel =
            ViewModelProvider(this, myViewModelFactory).get(AccountViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::accountView.isInitialized) {
            accountView = viewFactory.getAccountView(container)
        }
        return accountView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    private val accountViewStateObserver = Observer<AccountViewState> { state ->
        accountView.handleState(state)
    }

    private fun registerObservers() {
        accountView.registerListener(this)
        accountViewModel.screenState.observeForever(accountViewStateObserver)
    }

    private fun unregisterObservers() {
        accountView.unregisterListener(this)
        accountViewModel.screenState.removeObserver(accountViewStateObserver)
    }

    override fun onSignOutClick() {
        accountViewModel.signOutClick()
    }

    override fun onAboutClick() {
        mainNavigator.toAboutFragment()
    }

    override fun onContactDevClicked() {
        intentHandler.handleContactDev()
    }

    override fun onLibrariesClicked() {
        mainNavigator.toLibrariesFragment()
    }

}