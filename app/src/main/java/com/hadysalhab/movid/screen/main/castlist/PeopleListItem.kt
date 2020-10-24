package com.hadysalhab.movid.screen.main.castlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.common.constants.PROFILE_SIZE_h632
import com.hadysalhab.movid.screen.common.people.People
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class PeopleListItem(layoutInflater: LayoutInflater, parent: ViewGroup?) : BaseViewMvc() {
    private val peopleIV: ImageView
    private val peopleNameTV: TextView
    private val peopleRoleTV: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_people_list_item, parent, false))
        peopleIV = findViewById(R.id.iv_people)
        peopleNameTV = findViewById(R.id.tv_people_name)
        peopleRoleTV = findViewById(R.id.tv_people_role)


    }

    fun displayPeople(people: People) {
        peopleNameTV.text = people.peopleName
        peopleRoleTV.text = people.peopleRole
        if (people.profilePath == null) {
            Glide.with(getContext())
                .load(ContextCompat.getDrawable(getContext(), R.drawable.people_default_image))
                .into(peopleIV)
        } else {
            Glide.with(getContext())
                .load(IMAGES_BASE_URL + PROFILE_SIZE_h632 + people.profilePath)
                .into(peopleIV)
        }
    }
}
