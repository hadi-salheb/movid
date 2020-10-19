package com.hadysalhab.movid.screen.main.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

class AccountFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            AccountFragment()
    }

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    private lateinit var accountViewModel: AccountViewModel

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
        return inflater.inflate(R.layout.layout_account, container, false)
    }

}