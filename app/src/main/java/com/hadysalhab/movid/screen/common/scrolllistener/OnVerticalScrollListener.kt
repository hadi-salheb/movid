package com.hadysalhab.movid.screen.common.scrolllistener

import androidx.recyclerview.widget.RecyclerView

//  https://stackoverflow.com/questions/26543131/how-to-implement-endless-list-with-recyclerview
abstract class OnVerticalScrollListener : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!recyclerView.canScrollVertically(-1)) {
            onScrolledToTop()
        } else if (!recyclerView.canScrollVertically(1)) {
            onScrolledToBottom()
        } else if (dy < 0) {
            onScrolledUp()
        } else if (dy > 0) {
            onScrolledDown()
        }
    }

    abstract fun onScrolledUp()
    abstract fun onScrolledDown()
    abstract fun onScrolledToTop()
    abstract fun onScrolledToBottom()
}
