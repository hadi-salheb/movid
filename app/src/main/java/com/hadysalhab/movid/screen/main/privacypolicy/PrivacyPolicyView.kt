package com.hadysalhab.movid.screen.main.privacypolicy

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.getPrivacyPolicyHtml
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc
import org.sufficientlysecure.htmltextview.HtmlTextView

class PrivacyPolicyView(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) :
    BaseObservableViewMvc<PrivacyPolicyView.Listener>(), MenuToolbarLayout.Listener {
    interface Listener {
        fun onBackArrowClicked()
        fun onTagClicked(href: String?)
    }

    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout
    private val privacyPolicyTextView: HtmlTextView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_privacy_policy, parent, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar).also {
            it.showBackArrow()
            it.setToolbarTitle("PRIVACY POLICY")
        }
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.registerListener(this)
        privacyPolicyTextView = findViewById(R.id.html_text)
        privacyPolicyTextView.setHtml(getPrivacyPolicyHtml())
        privacyPolicyTextView.movementMethod = LinkMovementMethod.getInstance()
        privacyPolicyTextView.setOnClickATagListener { _, _, href ->
            listeners.forEach {
                it.onTagClicked(href)
            }
            true
        }
    }

    override fun onOverflowMenuIconClick() {

    }

    override fun onBackArrowClicked() {
        listeners.forEach {
            it.onBackArrowClicked()
        }
    }


}