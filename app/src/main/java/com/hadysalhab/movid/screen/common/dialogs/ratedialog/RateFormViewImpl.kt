package com.hadysalhab.movid.screen.common.dialogs.ratedialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.getRatingOptions

class RateFormViewImpl(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) : RateFormView() {
    private val titleTV: TextView
    private val negativeBtn: Button
    private val positiveBtn: Button

    private val spinner: Spinner
    private var ratePosition = 0
    private val spinnerAdapter: ArrayAdapter<Double>
    private val ratingOptions = getRatingOptions().reversed()

    init {
        setRootView(layoutInflater.inflate(R.layout.component_rating_form, viewGroup, false))
        titleTV = findViewById(R.id.txt_title)
        negativeBtn = findViewById(R.id.btn_negative)
        negativeBtn.setOnClickListener {
            listeners.forEach {
                it.onNegativeBtnClicked()
            }
        }

        positiveBtn = findViewById(R.id.btn_positive)
        positiveBtn.setOnClickListener {
            listeners.forEach {
                it.onPositiveBtnClicked()
            }
        }

        spinnerAdapter = ArrayAdapter(
            getContext(),
            android.R.layout.simple_spinner_item,
            ratingOptions
        )
        spinner = findViewById(R.id.rating_spinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                ratePosition = position
                listeners.forEach {
                    it.onRateChange(
                        ratingOptions[ratePosition]
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    override fun handleState(rateFormViewState: RateFormViewState) {
        titleTV.text = rateFormViewState.title

        val rateArg = rateFormViewState.rate
        val currentRate = ratingOptions[ratePosition]
        if (rateArg != currentRate) {
            val positionToBe = ratingOptions.indexOf(rateArg)
            spinner.setSelection(positionToBe)
        }
    }
}