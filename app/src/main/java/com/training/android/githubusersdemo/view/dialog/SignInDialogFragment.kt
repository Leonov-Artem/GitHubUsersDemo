package com.training.android.githubusersdemo.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.training.android.githubusersdemo.R

private val CLASS_NAME = SignInDialogFragment::class.java.simpleName
private val INTERFACE_NAME = SignInDialogFragment.NoticeDialogListener::class.java.simpleName

private const val ARG_MESSAGE = "ARG_MESSAGE"

class SignInDialogFragment : DialogFragment() {

    interface NoticeDialogListener {
        fun onSignInButtonClick()
    }

    private lateinit var listener: NoticeDialogListener

    private val message: String?
        get() = arguments?.getString(ARG_MESSAGE)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            val contextName = context::class.java.simpleName
            throw ClassCastException("$contextName must implement $CLASS_NAME.$INTERFACE_NAME")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_authorization)
            .setMessage(message)
            .setNeutralButton(R.string.dialog_button_hide) { _, _ -> }
            .setPositiveButton(R.string.dialog_button_sign_in) { dialog, id ->
                listener.onSignInButtonClick()
            }
            .create()

    companion object {
        val TAG: String = CLASS_NAME

        fun newInstance(message: String) = SignInDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_MESSAGE, message)
            }
        }
    }
}