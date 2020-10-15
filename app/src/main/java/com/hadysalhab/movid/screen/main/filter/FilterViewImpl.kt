package com.hadysalhab.movid.screen.main.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import java.util.*

class FilterViewImpl(
    layoutInflater: LayoutInflater,
    viewGroup: ViewGroup?,
    viewFactory: ViewFactory
) : FilterView() {
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout
    private val submitButton: Button

    //SortBy
    private val sortBySpinner: Spinner
    private var sortByPosition = 0
    private val sortBySpinnerAdapter: ArrayAdapter<String>
    private val sortByMap = mapOf<String, String>(
        "Popularity Descending" to "popularity.desc",
        "Popularity Ascending" to "popularity.asc",
        "Release Date Ascending" to "release_date.asc",
        "Release Date Descending" to "release_date.desc",
        "Revenue Ascending" to "revenue.asc",
        "Revenue Descending" to "revenue.desc",
        "Original Title Ascending" to "original_title.asc",
        "Original Title Descending" to "original_title.desc",
        "Vote Average Ascending" to "vote_average.asc",
        "Vote Average Descending" to "vote_average.desc",
        "Vote Count Ascending" to "vote_count.asc",
        "Vote Count Descending" to "vote_count.desc"
    )

    //includeAdult
    private val includeAdultSwitch: Switch

    //VoteCount
    private val releasedDateFromSpinner: Spinner
    private val releasedDateToSpinner: Spinner
    private val releasedDateSpinnerAdapter: ArrayAdapter<String>
    private val releasedDate =
        mutableListOf("") + (1900..Calendar.getInstance().get(Calendar.YEAR) + 5).reversed()
            .toList().map { it.toString() }
    private var releasedDateFromPosition = 0
    private var releasedDateToPosition = 0


    init {
        setRootView(layoutInflater.inflate(R.layout.layout_filter, viewGroup, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.setToolbarTitle("FILTER")

        //Submit
        submitButton = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            listeners.forEach {
                it.onFilterSubmit()
            }
        }

        //SortBy
        sortBySpinner = findViewById(R.id.sortBy_spinner)
        sortBySpinnerAdapter = ArrayAdapter(
            getContext(), android.R.layout.simple_spinner_item,
            sortByMap.keys.toList()
        )
        sortBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortBySpinner.adapter = sortBySpinnerAdapter
        sortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                listeners.forEach {
                    sortByPosition = position
                    it.onSortByChanged(sortByMap.getValue(sortBySpinnerAdapter.getItem(position)!!))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        //IncludeAdult
        includeAdultSwitch = findViewById(R.id.includeAdult_switch)
        includeAdultSwitch.setOnCheckedChangeListener { _, isChecked ->
            listeners.forEach {
                it.onIncludeAdultChanged(isChecked)
            }
        }


        //Released Year
        releasedDateFromSpinner = findViewById(R.id.released_from_spinner)
        releasedDateToSpinner = findViewById(R.id.released_to_spinner)
        releasedDateSpinnerAdapter = ArrayAdapter(
            getContext(),
            android.R.layout.simple_spinner_item,
            releasedDate
        )
        releasedDateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        releasedDateFromSpinner.adapter = releasedDateSpinnerAdapter
        releasedDateToSpinner.adapter = releasedDateSpinnerAdapter

        releasedDateFromSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    releasedDateFromPosition = position
                    listeners.forEach {
                        it.onPrimaryReleaseYearGteChanged(
                            if (position == 0) null else releasedDateSpinnerAdapter.getItem(
                                releasedDateFromPosition
                            )
                        )
                    }
                    if (releasedDateFromPosition != 0 && releasedDateToPosition != 0) {
                        if (releasedDateFromPosition < releasedDateToPosition) {
                            releasedDateToPosition = releasedDateFromPosition
                            releasedDateToSpinner.setSelection(releasedDateToPosition)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        releasedDateToSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                releasedDateToPosition = position
                listeners.forEach {
                    it.onPrimaryReleaseYearLteChanged(
                        if (position == 0) null else releasedDateSpinnerAdapter.getItem(
                            releasedDateToPosition
                        )
                    )
                }
                if (releasedDateToPosition != 0 && releasedDateFromPosition != 0) {
                    if (releasedDateFromPosition < releasedDateToPosition) {
                        releasedDateFromPosition = releasedDateToPosition
                        releasedDateFromSpinner.setSelection(releasedDateFromPosition)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun handleState(filterState: FilterState) {
        with(filterState) {
            handleIncludeAdultState(includeAdult)
            handleSortByState(sortBy)
            handleReleaseYearFrom(primaryReleaseYearGte)
            handleReleaseYearTo(primaryReleaseYearLte)
        }
    }

    private fun handleIncludeAdultState(includeAdult: Boolean) {
        if (includeAdultSwitch.isChecked != includeAdult) {
            includeAdultSwitch.isChecked = includeAdult
        }
    }

    private fun handleSortByState(sortByValue: String) {
        val currentSortByKey = sortBySpinnerAdapter.getItem(sortByPosition)
        if (sortByMap.getValue(currentSortByKey!!) != sortByValue) {
            val positionToBe = sortByMap.values.indexOf(sortByValue)
            sortBySpinner.setSelection(positionToBe)
        }
    }

    private fun handleReleaseYearFrom(releaseYearFrom: String?) {
        val currentReleaseYearFrom = releasedDateSpinnerAdapter.getItem(releasedDateFromPosition)
        val releaseYearFromArg = releaseYearFrom ?: ""
        if (currentReleaseYearFrom != releaseYearFromArg) {
            val positionToBe = releasedDateSpinnerAdapter.getPosition(releaseYearFromArg)
            releasedDateFromSpinner.setSelection(positionToBe)
        }
    }

    private fun handleReleaseYearTo(releaseYearTo: String?) {
        val currentReleaseYearTo = releasedDateSpinnerAdapter.getItem(releasedDateToPosition)
        val releaseYearToArg = releaseYearTo ?: ""
        if (currentReleaseYearTo != releaseYearToArg) {
            val positionToBe = releasedDateSpinnerAdapter.getPosition(releaseYearToArg)
            releasedDateToSpinner.setSelection(positionToBe)
        }
    }
}