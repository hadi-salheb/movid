package com.hadysalhab.movid.screen.common.cardgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc
import java.util.*

class DataGroup<K : Any>(val title: GroupType, val data: List<K>)

abstract class CardGroupViewImpl<Data : DataGroup<out Any>, ListenerType>(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?
) : BaseObservableViewMvc<ListenerType>() {
    protected val linearLayout: LinearLayout
    protected lateinit var groupType: GroupType
    private val groupTitle: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_movie_group, parent, false))
        linearLayout = findViewById(R.id.movie_linear_layout)
        groupTitle = findViewById(R.id.group_title)
    }

    fun renderData(data: Data, maxNumb: Int?) {
        this.groupType = data.title
        groupTitle.text = data.title.value.toUpperCase(Locale.ROOT).split("_").joinToString(" ")
        displayCardGroup(data, maxNumb)
    }

    protected abstract fun displayCardGroup(data: Data, maxNumb: Int?)
}







