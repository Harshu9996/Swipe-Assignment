package com.example.swipeassignment.ui.home.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.swipeassignment.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FeedbackDialog : DialogFragment() {
  //Dialog to be used to give feedback to users
    private var title: String? = null
    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            text = it.getString(ARG_TEXT)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(text)
            .setPositiveButton(getString(R.string.ok)) { dialog,id ->
                dismiss()
            }
            .create()

    companion object {

        @JvmStatic
        fun newInstance(title: String, text: String) =
            FeedbackDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_TEXT, text)
                }
            }

        const val ARG_TITLE = "title"
        const val ARG_TEXT = "text"
        const val TAG = "FeedbackDialog"
    }
}