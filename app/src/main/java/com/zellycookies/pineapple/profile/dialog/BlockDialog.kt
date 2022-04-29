package com.zellycookies.pineapple.profile.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.zellycookies.pineapple.R

class BlockDialog : DialogFragment() {
    private var result = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_block, null))
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    result = true
                }
                .setNegativeButton(R.string.no
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null.")
    }

    fun returnResult() : Boolean {
        return result
    }
}