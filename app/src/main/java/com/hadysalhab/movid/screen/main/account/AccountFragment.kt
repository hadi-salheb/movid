package com.hadysalhab.movid.screen.main.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.controllers.BaseFragment

class AccountFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            AccountFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_account, container, false)
    }

}