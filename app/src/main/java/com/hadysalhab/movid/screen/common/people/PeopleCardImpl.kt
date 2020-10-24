package com.hadysalhab.movid.screen.common.people

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.common.constants.PROFILE_SIZE_h632
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PeopleType : Parcelable {
    CREW,
    CAST
}

data class People(
    val peopleId: Int,
    val peopleName: String,
    val peopleRole: String,
    val profilePath: String?,
    val peopleType: PeopleType
)

class PeopleCardImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : PeopleCard() {
    private val peopleIV: ImageView
    private val peopleNameTV: TextView
    private val peopleRole: TextView
    private lateinit var people: People

    init {
        setRootView(layoutInflater.inflate(R.layout.component_people_card, parent, false))
        peopleIV = findViewById(R.id.iv_people)
        peopleNameTV = findViewById(R.id.tv_people_name)
        peopleRole = findViewById(R.id.tv_people_role)
        getRootView().setOnClickListener {
            listeners.forEach {
                it.onPeopleCardClicked(people.peopleId, people.peopleType)
            }
        }
    }

    override fun displayPeople(people: People) {
        this.people = people
        peopleNameTV.text = people.peopleName
        peopleRole.text = people.peopleRole
        if (people.profilePath == null) {
            Glide.with(getContext())
                .load(ContextCompat.getDrawable(getContext(), R.drawable.user_default_profile))
                .into(peopleIV)
        } else {
            Glide.with(getContext())
                .load(IMAGES_BASE_URL + PROFILE_SIZE_h632 + people.profilePath)
                .into(peopleIV)
        }
    }

}