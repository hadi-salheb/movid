package com.hadysalhab.movid.screen.common.dialogs.ratedialog

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.dialogs.BaseDialog
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject


private const val ARG_MOVIE_TITLE = "ARG_MOVIE_TITLE"
private const val ARG_CURRENT_RATING = "ARG_CURRENT_RATING"
private const val ARG_MOVIE_ID = "ARG_MOVIE_ID"

class RateDialogFragment : BaseDialog(), RateDialogScreen.Listener {
    companion object {
        @JvmStatic
        fun newInstance(movieTitle: String, currentRating: Double?, movieId: Int) =
            RateDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MOVIE_TITLE, movieTitle)
                    currentRating?.let {
                        putDouble(ARG_CURRENT_RATING, currentRating)
                    }
                    putInt(ARG_MOVIE_ID, movieId)
                }
            }
    }

    private var subscription: EventSource.NotificationToken? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var rateDialogScreen: RateDialogScreen

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    lateinit var rateDialogViewModel: RateDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        rateDialogViewModel =
            ViewModelProvider(this, myViewModelFactory).get(RateDialogViewModel::class.java)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        if (arguments == null) {
            throw IllegalStateException("RateDialog Arguments Musn't be null")
        }
        rateDialogScreen = viewFactory.getRateDialogScreen(null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(rateDialogScreen.getRootView())
        isCancelable = false
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private val rateViewStateObserver = Observer<RateViewState> {
        rateDialogScreen.handleState(it)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rateDialogScreen.registerListener(this)
        rateDialogViewModel.onStart(
            requireArguments().getString(ARG_MOVIE_TITLE)!!, requireArguments().getDouble(
                ARG_CURRENT_RATING
            ), requireArguments().getInt(ARG_MOVIE_ID)
        )
        rateDialogViewModel.rateDialogViewState.observeForever(rateViewStateObserver)
        subscription = rateDialogViewModel.screenEvents.startListening { event ->
            if (event == RateDialogEvent.Dismiss) {
                dismiss()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        rateDialogScreen.unregisterListener(this)
        rateDialogViewModel.rateDialogViewState.removeObserver(rateViewStateObserver)
        subscription?.stopListening()
        subscription = null
    }

    override fun onNegativeBtnClicked() {
        dismiss()
    }

    override fun onPositiveBtnClicked() {
        rateDialogViewModel.onPositiveBtnClicked()
    }

    override fun onRateChange(rate: Double) {
        rateDialogViewModel.onRateChange(rate)

    }
}