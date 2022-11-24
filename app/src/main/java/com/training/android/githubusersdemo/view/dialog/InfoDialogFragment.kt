package com.training.android.githubusersdemo.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.training.android.githubusersdemo.R

private const val ARG_INFO_MESSAGE = "ARG_INFO_MESSAGE"

class InfoDialogFragment : DialogFragment() {

    private val message: String?
        get() = arguments?.getString(ARG_INFO_MESSAGE)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_information)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_button_ok) { _, _ -> }
            .create()

    companion object {
        val TAG: String = InfoDialogFragment::class.java.simpleName

        fun newInstance(message: String) = InfoDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_INFO_MESSAGE, message)
            }
        }
    }
}