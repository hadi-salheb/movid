package com.hadysalhab.movid.screen.common.dialogs.infodialog

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.dialogs.BaseDialog
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

const val ARG_TITLE = "ARG_TITLE"
const val ARG_MESSAGE = "ARG_MESSAGE"
const val ARG_BUTTON_CAPTION = "ARG_BUTTON_CAPTION"

class InfoDialogFragment : BaseDialog(), InfoDialogView.Listener {
    companion object {
        @JvmStatic
        fun newInstance(title: String, message: String, caption: String) =
            InfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                    putString(ARG_BUTTON_CAPTION, caption)
                }
            }
    }

    @Inject
    lateinit var viewFactory: ViewFactory
    private lateinit var infoDialogView: InfoDialogView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        if (arguments == null) {
            throw IllegalStateException("InfoDialog Arguments Musn't be null")
        }
        infoDialogView = viewFactory.getInfoDialogView(null)
        infoDialogView.handleState(
            InfoDialogViewState(
                title = requireArguments().getString(ARG_TITLE) ?: "",
                message = requireArguments().getString(ARG_MESSAGE) ?: "",
                caption = requireArguments().getString(ARG_BUTTON_CAPTION) ?: ""

            )
        )
        val dialog = Dialog(requireContext())
        dialog.setContentView(infoDialogView.getRootView())

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        infoDialogView.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        infoDialogView.unregisterListener(this)
    }


    override fun onPositiveButtonClicked() {
        EventBus.getDefault().post(InfoDialogEvent.Positive)
        dismiss()
    }

}