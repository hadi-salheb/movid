package com.hadysalhab.movid.screen.main.filter

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.getVoteAverageNumbers
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.inputfilter.InputFilterMinMax
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import java.util.*

class FilterViewImpl(
    layoutInflater: LayoutInflater,
    viewGroup: ViewGroup?,
    viewFactory: ViewFactory
) : FilterView(), MenuToolbarLayout.Listener {
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout
    private val submitButton: Button
    private val resetButton: Button

    //SortBy
    private val sortByOptionsSpinner: Spinner
    private val sortByOrderSpinner: Spinner
    private var sortByOptionsPosition = 0
    private var sortByOrderPosition = 0
    private val sortByOptionsSpinnerAdapter: ArrayAdapter<String>
    private val sortByOrderSpinnerAdapter: ArrayAdapter<String>


    //includeAdult
    private val includeAdultSwitch: Switch

    //ReleasedDate
    private val releasedDateFromSpinner: Spinner
    private val releasedDateToSpinner: Spinner
    private val releasedDateSpinnerAdapter: ArrayAdapter<String>
    private val releasedDate =
        mutableListOf("") + (1900..Calendar.getInstance().get(Calendar.YEAR) + 5).reversed()
            .toList().map { it.toString() }
    private var releasedDateFromPosition = 0
    private var releasedDateToPosition = 0

    //VoteAverage
    private val voteAverageFromSpinner: Spinner
    private val voteAverageToSpinner: Spinner
    private val voteAverageSpinnerAdapter: ArrayAdapter<String>
    private val voteAverage = mutableListOf("") + getVoteAverageNumbers()
    private var voteAverageFromPosition = 0
    private var voteAverageToPosition = 0

    //VoteCount
    private val voteCountFromEditText: EditText
    private val voteCountToEditText: EditText

    //Runtime
    private val runtimeFromEditText: EditText
    private val runtimeToEditText: EditText

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_filter, viewGroup, false))
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.setToolbarTitle("FILTER")
        menuToolbarLayout.showBackArrow()
        menuToolbarLayout.registerListener(this)

        //Submit
        submitButton = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            listeners.forEach {
                it.onFilterSubmit()
            }
        }

        //Reset
        resetButton = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            listeners.forEach {
                it.onResetClick()
            }
        }

        //SortBy
        sortByOrderSpinner = findViewById(R.id.sortBy_order_spinner)
        sortByOptionsSpinner = findViewById(R.id.sortBy_spinner)
        sortByOptionsSpinnerAdapter = ArrayAdapter(
            getContext(), android.R.layout.simple_spinner_item,
            SortOption.values().map { it.sortOptionValue }
        )
        sortByOrderSpinnerAdapter = ArrayAdapter(
            getContext(),
            android.R.layout.simple_spinner_item,
            SortOrder.values().map { it.sortOrderValue }
        )
        sortByOptionsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortByOrderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortByOptionsSpinner.adapter = sortByOptionsSpinnerAdapter
        sortByOrderSpinner.adapter = sortByOrderSpinnerAdapter
        sortByOptionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                listeners.forEach {
                    sortByOptionsPosition = position
                    it.onSortByOptionChanged(SortOption.values()[sortByOptionsPosition])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        sortByOrderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                listeners.forEach {
                    sortByOrderPosition = position
                    it.onSortByOrderChanged(SortOrder.values()[sortByOrderPosition])
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

        voteAverageFromSpinner = findViewById(R.id.vote_average_from_spinner)
        voteAverageToSpinner = findViewById(R.id.vote_average_to_spinner)
        voteAverageSpinnerAdapter = ArrayAdapter(
            getContext(),
            android.R.layout.simple_spinner_item,
            voteAverage
        )
        voteAverageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        voteAverageFromSpinner.adapter = voteAverageSpinnerAdapter
        voteAverageToSpinner.adapter = voteAverageSpinnerAdapter

        voteAverageFromSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    voteAverageFromPosition = position
                    listeners.forEach {
                        it.onVoteAverageGteChanged(
                            if (position == 0) null else voteAverageSpinnerAdapter.getItem(
                                voteAverageFromPosition
                            )!!.toFloat()
                        )
                    }
                    if (voteAverageFromPosition != 0 && voteAverageToPosition != 0) {
                        if (voteAverageFromPosition > voteAverageToPosition) {
                            voteAverageToPosition = voteAverageFromPosition
                            voteAverageToSpinner.setSelection(voteAverageToPosition)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        voteAverageToSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                voteAverageToPosition = position
                listeners.forEach {
                    it.onVoteAverageLteChanged(
                        if (position == 0) null else voteAverageSpinnerAdapter.getItem(
                            voteAverageToPosition
                        )!!.toFloat()
                    )
                }
                if (voteAverageToPosition != 0 && voteAverageFromPosition != 0) {
                    if (voteAverageFromPosition > voteAverageToPosition) {
                        voteAverageFromPosition = voteAverageToPosition
                        voteAverageFromSpinner.setSelection(voteAverageFromPosition)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //VoteCount
        voteCountFromEditText = findViewById(R.id.vote_count_from_editText)
        voteCountToEditText = findViewById(R.id.vote_count_to_editText)
        voteCountFromEditText.filters = arrayOf<InputFilter>(
            InputFilterMinMax("1", "${Int.MAX_VALUE}")
        )
        voteCountToEditText.filters = arrayOf<InputFilter>(
            InputFilterMinMax("1", "${Int.MAX_VALUE}")
        )
        voteCountFromEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listeners.forEach {
                    if (s.isNullOrEmpty()) {
                        it.onVoteCountGteChanged(null)
                    } else {
                        it.onVoteCountGteChanged(s.toString().toInt())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        voteCountToEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listeners.forEach {
                    if (s.isNullOrEmpty()) {
                        it.onVoteCountLteChanged(null)
                    } else {
                        it.onVoteCountLteChanged(s.toString().toInt())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        runtimeFromEditText = findViewById(R.id.runtime_from_editText)
        runtimeToEditText = findViewById(R.id.runtime_to_editText)
        runtimeFromEditText.filters = arrayOf<InputFilter>(
            InputFilterMinMax("1", "${Int.MAX_VALUE}")
        )
        runtimeToEditText.filters = arrayOf<InputFilter>(
            InputFilterMinMax("1", "${Int.MAX_VALUE}")
        )
        runtimeFromEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listeners.forEach {
                    if (s.isNullOrEmpty()) {
                        it.onRuntimeGteChanged(null)
                    } else {
                        it.onRuntimeGteChanged(s.toString().toInt())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        runtimeToEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listeners.forEach {
                    if (s.isNullOrEmpty()) {
                        it.onRuntimeLteChanged(null)
                    } else {
                        it.onRuntimeLteChanged(s.toString().toInt())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun handleState(filterViewState: FilterViewState) {
        with(filterViewState) {
            handleIncludeAdultState(includeAdult)
            handleSortByOptionState(sortByOption)
            handleSortByOrderState(sortByOrder)
            handleReleaseYearFrom(primaryReleaseYearGte)
            handleReleaseYearTo(primaryReleaseYearLte)
            handleVoteAverageFrom(voteAverageGte)
            handleVoteAverageTo(voteAverageLte)
            handleVoteCountFrom(voteCountGte)
            handleVoteCountTo(voteCountLte)
            handleRuntimeFrom(withRuntimeGte)
            handleRuntimeTo(withRuntimeLte)
        }
    }

    private fun handleIncludeAdultState(includeAdult: Boolean) {
        if (includeAdultSwitch.isChecked != includeAdult) {
            includeAdultSwitch.isChecked = includeAdult
        }
    }

    private fun handleSortByOptionState(sortOption: SortOption) {
        val currentSortOptionValue = sortByOptionsSpinnerAdapter.getItem(sortByOptionsPosition)
        val currentSortOption =
            SortOption.values().find { it.sortOptionValue == currentSortOptionValue }
        if (currentSortOption != sortOption) {
            val positionToBe = SortOption.values().indexOf(sortOption)
            sortByOptionsSpinner.setSelection(positionToBe)
        }
    }

    private fun handleSortByOrderState(sortOrder: SortOrder) {
        val currentSortOrderValue = sortByOrderSpinnerAdapter.getItem(sortByOrderPosition)
        val currentSortOrder =
            SortOrder.values().find { it.sortOrderValue == currentSortOrderValue }
        if (currentSortOrder != sortOrder) {
            val positionToBe = SortOrder.values().indexOf(sortOrder)
            sortByOrderSpinner.setSelection(positionToBe)
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

    private fun handleVoteAverageFrom(voteAverageFrom: Float?) {
        val currentVoteAverageFrom = voteAverageSpinnerAdapter.getItem(voteAverageFromPosition)
        val voteAverageFromArg = voteAverageFrom?.toString() ?: ""
        if (currentVoteAverageFrom != voteAverageFromArg) {
            val positionToBe = voteAverageSpinnerAdapter.getPosition(voteAverageFromArg)
            voteAverageFromSpinner.setSelection(positionToBe)
        }
    }

    private fun handleVoteAverageTo(voteAverageTo: Float?) {
        val currentVoteAverageTo = voteAverageSpinnerAdapter.getItem(voteAverageToPosition)
        val voteAverageToArg = voteAverageTo?.toString() ?: ""
        if (currentVoteAverageTo != voteAverageToArg) {
            val positionToBe = voteAverageSpinnerAdapter.getPosition(voteAverageToArg)
            voteAverageToSpinner.setSelection(positionToBe)
        }
    }

    private fun handleVoteCountFrom(voteCountFrom: Int?) {
        val currentVote = voteCountFromEditText.text.toString()
        val voteCountFromArg = voteCountFrom?.toString() ?: ""
        if (currentVote != voteCountFromArg) {
            voteCountFromEditText.setText(voteCountFromArg)
        }
    }

    private fun handleVoteCountTo(voteCountTo: Int?) {
        val currentVote = voteCountToEditText.text.toString()
        val voteCountToArg = voteCountTo?.toString() ?: ""
        if (currentVote != voteCountToArg) {
            voteCountToEditText.setText(voteCountToArg)
        }
    }

    private fun handleRuntimeFrom(withRuntimeGte: Int?) {
        val currentVote = runtimeFromEditText.text.toString()
        val runtimeFromArgs = withRuntimeGte?.toString() ?: ""
        if (currentVote != runtimeFromArgs) {
            runtimeFromEditText.setText(runtimeFromArgs)
        }
    }

    private fun handleRuntimeTo(withRuntimeLte: Int?) {
        val currentVote = runtimeToEditText.text.toString()
        val runtimeToArgs = withRuntimeLte?.toString() ?: ""
        if (currentVote != runtimeToArgs) {
            runtimeToEditText.setText(runtimeToArgs)
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