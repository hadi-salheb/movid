package com.hadysalhab.movid.screen.main.icons

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc
import org.sufficientlysecure.htmltextview.HtmlTextView

class IconListItem(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?
) :
    BaseObservableViewMvc<IconListItem.Listener>() {
    interface Listener {
        fun onIconTagClicked(href: String)
    }

    private val iconTV: HtmlTextView
    private val iconIV: ImageView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_icon_list_item, parent, false))
        iconTV = findViewById(R.id.icon_text)
        iconIV = findViewById(R.id.icon_drawable)
    }

    fun displayIcon(icon: Icon,isDarkMode:Boolean) {
        iconIV.setImageResource(icon.drawable)
        if (!icon.isColored) {
            if (isDarkMode) {
                iconIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.white_milky))
            } else {
                iconIV.clearColorFilter()
            }
        }else{
            iconIV.clearColorFilter()
        }

        iconTV.setHtml(
            "<p>Icon made by <a href=\"${icon.authorUrl}\" target=\"_blank\">${icon.author}</a> from <a href=\"https://www.flaticon.com/home\" target=\"_blank\">www.flaticon.com</a></p>"
        )
        iconTV.setOnClickATagListener { _, _, href ->
            listeners.forEach { listener ->
                href?.let {
                    listener.onIconTagClicked(href)
                }
            }
            true
        }

    }
}