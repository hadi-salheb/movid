package com.hadysalhab.movid.screen.main.icons

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.screen.common.ViewFactory

class IconsAdapter(
    private val listener: Listener,
    private val viewFactory: ViewFactory
) :
    ListAdapter<Icon, IconViewHolder>(DIFF_CALLBACK),
    IconListItem.Listener {
    interface Listener {
        fun onIconTagClicked(href: String)
    }

    private var isDarkMode: Boolean = false

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Icon>() {
            override fun areItemsTheSame(oldItem: Icon, newItem: Icon): Boolean {
                return oldItem.drawable == newItem.drawable
            }

            override fun areContentsTheSame(oldItem: Icon, newItem: Icon): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun displayIconsWithDarkMode(icons: List<Icon>){
        submitList(icons)
        this.isDarkMode = true
    }

    fun displayIconsWithLightMode(icons: List<Icon>){
        submitList(icons)
        this.isDarkMode = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = viewFactory.getIconListItem(parent)
        view.registerListener(this)
        return IconViewHolder(view)
    }


    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(getItem(position), isDarkMode)
    }

    override fun onIconTagClicked(href: String) {
        listener.onIconTagClicked(href)
    }


}